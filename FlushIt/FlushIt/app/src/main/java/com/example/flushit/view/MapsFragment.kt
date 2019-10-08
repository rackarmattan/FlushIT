package com.example.flushit.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.model.*
import com.example.flushit.utilities.InjectorUtils
import com.example.flushit.viewmodel.ToiletsViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView


class MapsFragment : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    ToiletDialog.ToiletDialogListener {

    private lateinit var mView: View
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private lateinit var fragmentContext: FragmentActivity

    private val recentZoomLevel = 13f

    private lateinit var toiletViewModel: ToiletsViewModel

    private lateinit var map: GoogleMap

    /**
     * Location request code and
     */
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    /**
     * Gets the FragmentActivity that is used to show the ToiletDialog.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context as FragmentActivity
    }

    /**
     * Sets the side menu item that corresponds to this fragment to checked.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val nav = activity?.findViewById<NavigationView>(R.id.nav_view)
        val menu = nav?.menu
        val menuItem = menu?.findItem(R.id.nav_map)
        menuItem?.let {
            if(!it.isChecked) it.isChecked = true
        }
    }

    /**
     * Creates the fragment_maps.xml view. Sets the title of the action bar to reflect this
     * fragment's content. Retrieves the map fragment frfom the xml file to connect this fragment
     * with the map.
     *
     * Sets up the location callback to constantly update the lastLocation property to be the user's
     * last location. After that, the location request is created.
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mView = inflater.inflate(R.layout.fragment_maps, container, false)

        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity!!)

        (activity as? AppCompatActivity)?.let { it.supportActionBar?.title = resources.getString(R.string.map) }

        initializeToiletsViewModel()

        if (mapFragment == null) {
            val fm = fragmentManager
            val ft = fm?.beginTransaction()
            mapFragment = SupportMapFragment.newInstance()
            ft?.replace(R.id.map, mapFragment!!)?.commit()
        }

        mapFragment?.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                if (p0 != null) {
                    lastLocation = p0.lastLocation
                }
            }
        }

        createLocationRequest()
        return mView
    }

    /**
     * Checks if the user has given permission to location. If not, permission is requested.
     *
     * After that, the blue dot on the map is added, which is indicating where the user currently
     * is. The user's location is then retrieved by the lastLocation property from fused location client.
     * If it was possible to get the latest location, the camera is moved to that place.
     */

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        this.map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this.activity!!) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLang = LatLng(location.latitude, location.longitude)
                this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang, recentZoomLevel))
            }
        }
    }

    /**
     * Initialize this class viewmodel.
     */
    private fun initializeToiletsViewModel() {
        val factory = InjectorUtils.provideToiletsViewModelFactory()
        toiletViewModel = ViewModelProviders.of(this, factory).get(ToiletsViewModel::class.java)
    }

    /**
     * Listen for changes in the list of toilet's via the viewmodel. If something changed, redraw the
     * toilet markers on the map. Set the marker's tag to the current toilet in order to detect which
     * toilet that was clicked on later.
     */
    private fun placeToiletMarkers() {
        toiletViewModel.getToilets().observe(this, Observer { toiletsSnapshot ->
            for (snapShot in toiletsSnapshot.children) {
                val toilet = snapShot.getValue(Toilet::class.java)
                toilet?.let { map.addMarker(createMarkerOptions(it)).tag = it }
            }
        })
    }

    /**
     * Enables zooming on the map and sets this class as the callback when markers are clicked.
     *
     * Initialize this class viewmodel and place all the toilet markers on the map. After that,
     * call the setUpMap method.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(context)

        map = googleMap

        with(map) {
            uiSettings.isZoomControlsEnabled = true
            setOnMarkerClickListener(this@MapsFragment)
        }

        placeToiletMarkers()

        setUpMap()

    }

    /**
     * Callback method when a marker on the map was clicked.
     *
     * If the tag that the marker has can be casted as a Toilet, call the showDialog method and
     * return true, otherwise return false.
     * @param p0 : The marker that was clicked
     */
    override fun onMarkerClick(p0: Marker?): Boolean {
        showDialog(p0?.tag as? Toilet ?: return false)
        return true
    }

    /**
     * Is called when a marker on the map is clicked. Creates a ToiletDialog object,
     * sets the dialog's target fragment as this is order to listen for changes in
     * the dialog. Put the toilet that was clicked on in a Bundle in order for the
     * dialog to know which toilet that was clicked. Lastly, show the ToiletDialog.
     * @param toilet : The toilet to show in the dialog
     */
    private fun showDialog(toilet: Toilet) {
        val dialog = ToiletDialog()
        dialog.setTargetFragment(this, 0)
        val data = Bundle()
        data.putParcelable("toilet", toilet)
        dialog.arguments = data
        dialog.show(fragmentContext.supportFragmentManager, "Toilet dialog")
    }

    /**
     * If permission isn't granted for receiving location updates, request it now. If permission is
     * granted, request for location updates.
     */
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    /**
     * Creates a location request and add it to the builder. Sets the interval which specifies
     * the rate that the updates are requested.
     *
     * Before requesting location updates, the user's settings need to be checked (hard to ask for
     * location updates if the GPS is off). The settings client handles this check. If the task is
     * successful, a location request can be initiated. If the task failed, there is something wrong
     * with the user's settings. If the exception is resolvable, show a dialog that gives the user the
     * opportunity to change the settings.
     */
    private fun createLocationRequest() {
        locationRequest = LocationRequest()

        locationRequest.interval = 10000

        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this.activity!!)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@MapsFragment.activity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    println("Error")
                }
            }
        }
    }

    /**
     * Start location updates if the request code is ok.
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    /**
     * Stop location updates if the fragment is paused.
     */

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Start location updates when the fragment is resumed.
     */
    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    /**
     * Callback method for the ToiletDialog. Rates the toilet via this class viewmodel.
     * @param toilet : The toilet to rate
     * @param rating : The grade that the toilet got
     */
    override fun rateToilet(toilet: Toilet, rating: Float) {
        toiletViewModel.rateToilet(rating, toilet)
    }

    /**
     * Creates a marker that can be draawned on the map. Takes in a toilet and takes the
     * toilet's latitude and longitude and set the marker options' position to that.
     * Sets the icon to a pink icon of a toilet.
     * @param toilet : The toilet to add to the map
     * @return MarkerOptions with an icon and the toilet's position
     */
    private fun createMarkerOptions(toilet: Toilet): MarkerOptions {
        val position = LatLng(toilet.latitude, toilet.longitude)
        val icon = BitmapDescriptorFactory.fromResource(R.mipmap.toilet100pink)
        return MarkerOptions().position(position).title(toilet.name).icon(icon)
    }

    /**
     * This functions sets up the current location the first time the user gives permission to
     * do so. Otherwise, this is done in the method SetUpMap() and StartLocationUpdates().
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            val requestLocation = ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            if(requestLocation){
                this.map.isMyLocationEnabled = true

                fusedLocationClient.lastLocation.addOnSuccessListener(this.activity!!) { location ->
                    if (location != null) {
                        lastLocation = location
                        val currentLatLang = LatLng(location.latitude, location.longitude)
                        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang, recentZoomLevel))
                    }
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }
    }

}

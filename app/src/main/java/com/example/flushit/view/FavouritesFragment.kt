package com.example.flushit.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flushit.R
import com.example.flushit.model.*
import com.example.flushit.utilities.InjectorUtils
import com.example.flushit.viewmodel.UserToiletsViewModel
import com.example.flushit.viewmodel.UserViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot


/**
 * Presenter class for the rated toilets view. This class retrieves the current user
 * by an event from the MainNavigationHandler's viewmodel. When the current user is set, this class loads
 * the recycler view and its adapter class Toilet adapter with the retrieved user information.
 * If the user's permission level is admin though, this class make a 'secret button' visible
 * that leads the admin user to an upload page.
 */

class FavouritesFragment : Fragment() {

    private lateinit var secretButton: Button

    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: ToiletAdapter

    private lateinit var recyclerView: RecyclerView


    private val permissionLevelAdmin = "admin"
    private val permissionLevelUser = "user"

    //TEST 12/9
    private lateinit var userToiletsViewModel: UserToiletsViewModel


    /**
     * When the MainNavigationHandler navigates to this fragment, this fragment's item in the side menu is marked.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val nav = activity?.findViewById<NavigationView>(R.id.nav_view)
        val menu = nav?.menu
        val menuItem = menu?.findItem(R.id.nav_favorites)
        menuItem?.let {
            if (!it.isChecked) it.isChecked = true
        }
    }

    /**
     * Creates the view with the user's rated toilets and sets the title to reflect this view's
     * content.
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        secretButton = view.findViewById(R.id.secretButton)
        secretButton.setOnClickListener { openSecretPage() }

        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.title = resources.getString(R.string.rated_toilets)
        }
        //activity?.let { userViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java) }


        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

//        userViewModel.getCurrentUser().observe(this, Observer {user->
//            context?.let{adapter = ToiletAdapter(it, userViewModel, user)}
//            recyclerView.adapter = adapter
//        })

        val factory = InjectorUtils.provideUserToiletsViewModelFactory()
        userToiletsViewModel = ViewModelProviders.of(this, factory).get(UserToiletsViewModel::class.java)
        context?.let {
            adapter = ToiletAdapter(it, userToiletsViewModel)
            recyclerView.adapter = adapter
        }

        //initializeUi()
        //observeUser()
        testUserToiletsViewModel()

        return view
    }

    private fun testUserToiletsViewModel(){
        userToiletsViewModel.getUserToilets().observe(this, Observer {
            println("INSIDE OBSERVE NEW RATING SYSTEM SIZE OF LIST: ${it.size}")
            it.forEach { println("INSIDE FOREACH OF NEW RATING SYSTEM, NAME : ${it.toilet?.name}") }
            adapter.setRatings(it)
        })
    }

    private fun observeUser(){
        userViewModel.getCurrentUser().observe(this, Observer {
            if(it.permissionLevel.equals("admin")){
                secretButton.visibility = View.VISIBLE
            }
            else{
                secretButton.visibility = View.GONE
            }
        })
    }

    /**
     * This function gets the viewmodel from the activity. When the current user is set
     * from the viewmodel, the user's list with rated toilets is sent to the adapter class.
     * If the current user's permission level is 'admin' the "secret button" is displayed.
     */
    private fun initializeUi() {

//        userViewModel.getToiletsRatedByUser().observe(this, Observer { toiletList ->
//            println("INSIDE GET TOILETS FAVOURITES LIST : " + toiletList.toString())
//            toiletList.forEach { println(it) }
//            adapter.setToilets(toiletList)
//        })
        userViewModel.getToiletsRatedByUser().observe(this, Observer { ratings ->
            println("Inside observe toilets " + ratings)
            adapter.setRatings(ratings)
        })
//
//
//        userViewModel.getCurrentUser().observe(this, Observer { userSnapshot ->
//            val user = userSnapshot.getValue(User::class.java)
//            val tmpContext = context
//            tmpContext?.let {
//                user?.let {
//                    userViewModel.getToiletsRatedByUser(user)?.observe(this, Observer {
//                        val adapter = ToiletAdapter(tmpContext, it, userViewModel, user)
//                        recyclerView.adapter = adapter
//                    })
//
//                    if(user.permissionLevel.equals(permissionLevelAdmin)){
//                        secretButton.visibility = View.VISIBLE
//                    }
//                    else{
//                        secretButton.visibility = View.GONE
//                    }
//                }
//            }
//        })
    }

    /**
     * Click listener for the secret button. Opens the upload page.
     */
    private fun openSecretPage() {
        val intent = Intent(context, UploadActivity::class.java)
        startActivity(intent)
    }

}
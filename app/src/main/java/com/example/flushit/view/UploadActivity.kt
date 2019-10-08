package com.example.flushit.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.model.Toilet
import com.example.flushit.utilities.InjectorUtils
import com.example.flushit.viewmodel.ToiletsViewModel
import kotlinx.android.synthetic.main.activity_upload.*

/**
 * Secret upload page for admins (regular user's cannot see this page). Creates the activity_upload.xml.
 */
class UploadActivity : AppCompatActivity() {

    private lateinit var toiletViewModel: ToiletsViewModel
    private var imageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1;
    }

    /**
     * Creates the viewmodel for this class and sets listeners for the buttons.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        chooseFileButtonUpload.setOnClickListener { openFileChooser() }
        addToiletButtonUpload.setOnClickListener { uploadToilet() }

        initializeToiletViewModel()
    }

    /**
     * Checks the permission, if the permission is OK, set the image uri to the data that
     * was chosen.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null &&
            data.data != null
        ) {
            imageUri = data.data!!
        }
    }

    /**
     * Initialize the viewmodel for this class.
     */
    private fun initializeToiletViewModel(){
        val factory = InjectorUtils.provideToiletsViewModelFactory()
        toiletViewModel = ViewModelProviders.of(this, factory).get(ToiletsViewModel::class.java)
    }

    /**
     * Callback method for the open file button. Creates an intent and sets its type to image to open
     * the image folder on the phone. Request permission to get files.
     */
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    /**
     * Get all the text from the text fields in the xml file. Check if they are filled and is they are,
     * try to upload the information through the viewmodel.
     */
    private fun uploadToilet() {
        val imageUriChecked = imageUri
        val name = editTextToiletName.text.toString().trim()
        val latitude = editTextLat.text.toString().trim().toDouble()
        val longitude = editTextLong.text.toString().trim().toDouble()
        val price = editTextPrice.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        if(imageUriChecked != null && name.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty()){
            val toilet = Toilet(" ", name, latitude, longitude, description, price, 0f, 0, 0f, " ")
            toiletViewModel.uploadToilet(toilet, this, imageUriChecked)
        }
    }

}

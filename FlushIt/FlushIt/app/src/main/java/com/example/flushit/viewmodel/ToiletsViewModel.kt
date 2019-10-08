package com.example.flushit.viewmodel

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import com.example.flushit.model.Repository
import com.example.flushit.model.Toilet

/**
 * The viewmodel for toilets. This class handles communication between the views and the repository.
 * As this class is persistent when the activities/fragments are destroyed the data doesn't need
 * to be reloaded from the internet.
 * @param repository : The repository that this viewmodel gets data from.
 */
class ToiletsViewModel(private val repository: Repository) : ViewModel() {

    /**
     * Get the toilets from the repository.
     * @return The toilets from the repository
     */
    fun getToilets() = repository.getToilets()

    /**
     * Update a toilet in the repository.
     * @param toilet : The toilet to update
     */
    fun updateToilet(toilet: Toilet){
        repository.updateToilet(toilet)
    }

    /**
     * Rate a toilet. Takes the toilet's number of raters, adds one to that. Takes the total grade and
     * adds the new grade to that. Then recalculates the averege grade that this toilet has. Update
     * this toilet in the repository with the new rating.
     * @param rating : The new grade that the toilet got
     * @param toilet : The toilet that was rated
     */
    fun rateToilet(rating: Float, toilet: Toilet){
        toilet.nrOfRaters++
        toilet.totalRating += rating
        toilet.averageRating = toilet.totalRating / toilet.nrOfRaters
        updateToilet(toilet)
    }

    /**
     * Gets the file extension from a uri and returns it as a string.
     * @return The file extension as string
     */
    private fun getFileExtension(context: Context, uri: Uri) : String?{
        val cr = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr?.getType(uri))
    }

    /**
     * Upload a new toilet through the repository.
     * @param toilet : The toilet to upload
     * @param context : Context to be able to get the file extension
     * @param uri : The uri of the image that belongs to the toilet
     */
    fun uploadToilet(toilet:Toilet, context: Context, uri: Uri){
        val fileExtension = getFileExtension(context, uri)
        fileExtension?.let { repository.uploadToilet(toilet, it, uri) }
    }

}
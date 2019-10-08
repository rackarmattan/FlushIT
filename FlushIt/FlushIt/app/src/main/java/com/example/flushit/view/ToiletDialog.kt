package com.example.flushit.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.model.Toilet
import com.example.flushit.model.User
import com.example.flushit.viewmodel.UserViewModel
import com.squareup.picasso.Picasso
import java.lang.ClassCastException
import java.lang.IllegalStateException

/**
 * Dialog class that displays a toilet that the user has clicked on. Creates the toilet_dialog.xml.
 */

class ToiletDialog : AppCompatDialogFragment() {
    private lateinit var imageView: ImageView
    private lateinit var toilet: Toilet
    private lateinit var toiletName: TextView
    private lateinit var toiletPrice: TextView
    private lateinit var toiletDescription: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var averageScoreText: TextView
    private lateinit var currentUser: User
    private val toiletKey = "toilet"
    private lateinit var userViewModel: UserViewModel

    /**
     * The class that created the dialog.
     */
    private lateinit var listener: ToiletDialogListener

    /**
     * Interface for the class that opened the dialog. Enables callbacks to the creator class.
     */
    interface ToiletDialogListener{
        /**
         * Makes it able to rate this toilet.
         * @param toilet : This dialog's toilet.
         * @param rating : The amount of points that the toilet got
         */
        fun rateToilet(toilet:Toilet, rating: Float)
    }

    /**
     * Set the listener fo the class to the class that created the dialog.
     */
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        try{
            listener = targetFragment as ToiletDialogListener
        }
        catch(e: ClassCastException){
            throw ClassCastException("Calling fragment must implement toilet dialog listener. Target fragment: " + targetFragment.toString())
        }
    }

    /**
     * Creates the dialog view. The toilet that was pressed on has to be passed via the class that
     * created this dialog.
     *
     * Retrieves the userviewmodel from the main activity (the MainNavigationHandler) and also the current user.
     *
     * After that, set all the information in the view to the information in this toilet.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let { toilet = it.getParcelable(toiletKey) }

        return activity?.let {
            userViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java)
            userViewModel.getCurrentUser().observe(this, Observer {
                val snapShotUser = it.getValue(User::class.java)
                snapShotUser?.let { currentUser = it }
            })

            val builder = AlertDialog.Builder(it)
            val view = it.layoutInflater.inflate(R.layout.toilet_view, null)
            builder.setView(view)
            toiletName = view.findViewById(R.id.toiletTextName)
            toiletPrice = view.findViewById(R.id.toiletTextPrice)
            toiletDescription = view.findViewById(R.id.toiletTextDescription)
            imageView = view.findViewById(R.id.toiletImageView)
            ratingBar = view.findViewById(R.id.ratingBar)
            averageScoreText = view.findViewById(R.id.averageNumberText)
            ratingBar.setOnRatingBarChangeListener { bar, rating, fromUser -> rateToilet(bar, rating, fromUser) }
            ratingBar.rating = toilet.averageRating
            toiletName.text = toilet.name
            toiletPrice.text = toilet.price
            toiletDescription.text = toilet.description
            averageScoreText.text = resources.getString(R.string.average_rating_text, "%.2f".format(toilet.averageRating), toilet.nrOfRaters)
            Picasso.get().load(toilet.imageURL).rotate(90f).fit().centerCrop().into(imageView)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Listener for the rating bar (the stars). If the press was from a user, make the listener rate this
     * toilet with the provided information. Also add this toilet to the current user's list of rated
     * toilets via the userViewmodel. After that, set the ratingBar to disabled to indicate that it
     * was pressed.
     */
    private fun rateToilet(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
        if (fromUser) {
            Toast.makeText(context, "Rating pressed: " + rating, Toast.LENGTH_SHORT).show()
            listener.rateToilet(toilet, rating)
            userViewModel.addToRatedToilets(currentUser, toilet)
            ratingBar.isEnabled = false
        }
    }

    /**
     * Save the current toilet if this view is destroyed.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(toiletKey, toilet)
    }

    /**
     * Retrieve the current toilet when this view is recreated.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            toilet = it.getParcelable(toiletKey)
        }
    }
}
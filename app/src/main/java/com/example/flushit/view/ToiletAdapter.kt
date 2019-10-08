package com.example.flushit.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.flushit.R
import com.example.flushit.model.Rating
import com.example.flushit.model.Toilet
import com.example.flushit.model.User
import com.example.flushit.viewmodel.UserToiletsViewModel
import com.example.flushit.viewmodel.UserViewModel
import com.squareup.picasso.Picasso

/**
 * Adapter class for the recycler view in the "fragments_favourites.xml". This class takes in
 * the list with the toilets to display in the "toilet_card_item.xml" file.
 * @param context : The current context
 * @param toilets : The toilets to display in the recycler view
 */
class ToiletAdapter(
    private var context: Context,
    private var viewModel: UserToiletsViewModel
) : RecyclerView.Adapter<ToiletAdapter.ToiletViewHolder>() {

    private var toilets: List<Toilet> = listOf()
    private var ratings: List<Rating> = listOf()

    /**
     * Creates a view ("toilet_card_item.xml") and sets it to the ToiletViewHolder and returns the holder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToiletViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.toilet_card_item, parent, false)
        return ToiletViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ratings.size
    }

    /**
     * Gets a ToiletViewHolder and fills the holders variables with information from the toilet list.
     */
    override fun onBindViewHolder(holder: ToiletViewHolder, position: Int) {
//        val current = toilets[position]
//        holder.textViewName.text = current.name
//        holder.deleteButton.setOnClickListener { deleteToilet(current, position) }
//        Picasso.get().load(current.imageURL).rotate(90f).fit().centerCrop().into(holder.imageView)
        val current = ratings[position]
        holder.textViewName.text = current.toilet?.name
        current.toilet?.let {
            val tmp = it
            holder.deleteButton.setOnClickListener { deleteToilet(tmp, position, current.grade) }
            Picasso.get().load(tmp.imageURL).rotate(90f).fit().centerCrop().into(holder.imageView)
            holder.gradeTextView.text = context.resources.getString(R.string.your_grade, "%.2f".format(current.grade))
        }

    }

    private fun deleteToilet(toiletToDelete: Toilet, position: Int, grade: Float) {
        println("Delete toilet clicked " + toiletToDelete.name)
        viewModel.removeToiletFromUser(toiletToDelete, grade)
        //kanske ta bort
        notifyItemRemoved(position)
    }

    fun setToilets(_toilets: List<Toilet>) {
        toilets = _toilets
        //uppdatera när listan laddat klart
        notifyDataSetChanged()
    }

    fun setRatings(_ratings: List<Rating>) {
        ratings = _ratings
        notifyDataSetChanged()
    }

    /**
     * Class that holds the views in the recycler view.
     */
    class ToiletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName: TextView = itemView.findViewById(R.id.toilet_name_text)
        var imageView: ImageView = itemView.findViewById(R.id.toilet_image_view)
        var deleteButton: Button = itemView.findViewById(R.id.delete_button)
        var gradeTextView: TextView = itemView.findViewById(R.id.grade_text)
    }

}
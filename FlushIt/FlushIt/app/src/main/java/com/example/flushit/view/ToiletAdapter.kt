package com.example.flushit.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flushit.R
import com.example.flushit.model.Toilet
import com.squareup.picasso.Picasso

/**
 * Adapter class for the recycler view in the "fragments_favourites.xml". This class takes in
 * the list with the toilets to display in the "toilet_card_item.xml" file.
 * @param context : The current context
 * @param toilets : The toilets to display in the recycler view
 */
class ToiletAdapter(
    private var context: Context,
    private var toilets: List<Toilet>
) : RecyclerView.Adapter<ToiletAdapter.ToiletViewHolder>() {

    /**
     * Creates a view ("toilet_card_item.xml") and sets it to the ToiletViewHolder and returns the holder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToiletViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.toilet_card_item, parent, false)
        return ToiletViewHolder(view)
    }

    override fun getItemCount(): Int {
        return toilets.size
    }

    /**
     * Gets a ToiletViewHolder and fills the holders variables with information from the toilet list.
     */
    override fun onBindViewHolder(holder: ToiletViewHolder, position: Int) {
        val current = toilets.get(position)
        holder.textViewName.text = current.name
        Picasso.get().load(current.imageURL).rotate(90f).fit().centerCrop().into(holder.imageView)

    }

    /**
     * Class that holds the views in the recycler view.
     */
    class ToiletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName: TextView = itemView.findViewById(R.id.toilet_name_text)
        var imageView: ImageView = itemView.findViewById(R.id.toilet_image_view)

    }
}
package com.gfg.example_recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wheelsonwheels.data.model.Listing
import com.example.wheelsonwheels.R

class ListingAdapter(private val listings: List<Listing>) :
    RecyclerView.Adapter<ListingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_card, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, index: Int) {
        val listing = listings[index]

        holder.listingName.text = listing.title
        holder.listingCategory.text = listing.category
        holder.listingPrice.text = listing.price.toString()//"%,.2f".format(listing.price)

        // photos not implemented yet. need to figure out how we can store them if we do add
        //if(listing.photos[0] != null)
        //    holder.listingPhoto.setImageResource(listing.photos[0]!!)
    }

    override fun getItemCount(): Int {
        return listings.size
    }

    // ViewHolder class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listingName: TextView = itemView.findViewById(R.id.name)
        val listingCategory: TextView = itemView.findViewById(R.id.category)
        val listingPrice: TextView = itemView.findViewById(R.id.price)
        val listingPhoto: ImageView = itemView.findViewById(R.id.photo)
    }
}
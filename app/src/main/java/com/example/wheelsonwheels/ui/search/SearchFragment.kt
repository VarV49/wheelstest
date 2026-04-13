package com.example.wheelsonwheels.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wheelsonwheels.R
import com.example.wheelsonwheels.data.model.ItemCondition
import com.example.wheelsonwheels.data.model.Listing
import com.gfg.example_recyclerview.ListingAdapter
import java.util.Date

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnReturn = view.findViewById<Button>(R.id.btnReturn)
        val recyclerView = view.findViewById<RecyclerView>(R.id.listingList)

        val testListings: MutableList<Listing> = arrayListOf()
        for (i in 0..12) {
            testListings.add(
                Listing(
                    id = i.toLong(),
                    sellerID = 1,
                    title = "hi",
                    description = "hello",
                    category = "tire",
                    price = 2000,
                    condition = ItemCondition.NEW,
                    stock = 100,
                    createdAt = Date()
                )
            )
        }

        print(testListings.get(0))
        print("DONE.")

        recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = ListingAdapter(testListings)
        recyclerView.adapter = adapter

        btnReturn.setOnClickListener {
            findNavController().navigate(R.id.action_search_to_home)
        }
    }

}
package com.example.wheelsonwheels.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wheelsonwheels.R
import com.example.wheelsonwheels.data.model.UserRole
import com.example.wheelsonwheels.viewmodel.AuthViewModel

class HomeFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvRole = view.findViewById<TextView>(R.id.tvRole)
        val btnBrowse = view.findViewById<Button>(R.id.btnBrowse)
        val btnCart = view.findViewById<Button>(R.id.btnCart)
        val btnOrders = view.findViewById<Button>(R.id.btnOrders)
        val btnMyListings = view.findViewById<Button>(R.id.btnMyListings)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Populate user info
        val user = authViewModel.currentUser
        if (user != null) {
            tvWelcome.text = "Welcome, ${user.name}!"
            tvEmail.text = user.email
            tvRole.text = "Role: ${user.role.name}"

            // Show My Listings only for sellers and admins
            if (user.role == UserRole.SELLER || user.role == UserRole.ADMIN) {
                btnMyListings.visibility = View.VISIBLE
            }

            // Hide cart and orders for sellers
            if (user.role == UserRole.SELLER) {
                btnCart.visibility = View.GONE
                btnOrders.visibility = View.GONE
            }
        }

        // These navigate to your teammates' fragments
        // They just need to add the destinations to nav_graph.xml
        btnBrowse.setOnClickListener {
            // findNavController().navigate(R.id.action_home_to_search)
        }

        btnCart.setOnClickListener {
            // findNavController().navigate(R.id.action_home_to_cart)
        }

        btnOrders.setOnClickListener {
            // findNavController().navigate(R.id.action_home_to_orders)
        }

        btnMyListings.setOnClickListener {
            // findNavController().navigate(R.id.action_home_to_listings)
        }

        btnLogout.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(R.id.action_home_to_login)
        }
    }
}
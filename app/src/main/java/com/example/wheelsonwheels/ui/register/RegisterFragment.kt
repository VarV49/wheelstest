package com.example.wheelsonwheels.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wheelsonwheels.R
import com.example.wheelsonwheels.data.model.UserRole
import com.example.wheelsonwheels.viewmodel.AuthState
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText

class RegisterFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val spinnerRole = view.findViewById<Spinner>(R.id.spinnerRole)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val btnGoToLogin = view.findViewById<Button>(R.id.btnGoToLogin)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        val roles = listOf("BUYER", "SELLER")
        spinnerRole.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    tvError.visibility = View.GONE
                    btnRegister.isEnabled = false
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    findNavController().navigate(R.id.action_register_to_home)
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    tvError.text = state.message
                    tvError.visibility = View.VISIBLE
                }
                else -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                }
            }
        }

        btnRegister.setOnClickListener {
            val role = UserRole.valueOf(spinnerRole.selectedItem.toString())
            authViewModel.register(
                etName.text.toString().trim(),
                etEmail.text.toString().trim(),
                etPassword.text.toString(),
                role
            )
        }

        btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }
}
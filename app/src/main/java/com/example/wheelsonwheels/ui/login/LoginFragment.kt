package com.example.wheelsonwheels.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wheelsonwheels.R
import com.example.wheelsonwheels.viewmodel.AuthState
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnGoToRegister = view.findViewById<Button>(R.id.btnGoToRegister)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    tvError.visibility = View.GONE
                    btnLogin.isEnabled = false
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    findNavController().navigate(R.id.action_login_to_home)
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    tvError.text = state.message
                    tvError.visibility = View.VISIBLE
                }
                else -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                }
            }
        }

        btnLogin.setOnClickListener {
            authViewModel.login(
                etEmail.text.toString().trim(),
                etPassword.text.toString()
            )
        }

        btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }
}
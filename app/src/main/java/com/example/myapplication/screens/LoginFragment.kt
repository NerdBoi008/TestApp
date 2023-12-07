package com.example.myapplication.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.model.FirebaseAuthentication
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import java.util.regex.Pattern

private const val TAG = "LoginFragment"

class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var errorMessageHeader: LinearLayout
    private lateinit var linearLoader: LinearProgressIndicator

//    private val viewModel: AppViewModel by viewModels()
//    private val auth = viewModel.getFirebaseAuthInstance()
    private val auth = FirebaseAuthentication.getFirebaseInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        navController = findNavController()
        errorMessageHeader = binding.invalidCredentailsMessage
        linearLoader = binding.linearLoader

        binding.loginButton.setOnClickListener {
            signInUser()
        }

        binding.forgotPassword.setOnClickListener {

        }

        binding.signUpButton.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_signupFragment)
        }

        return binding.root
    }

    private fun signInUser() {
        val email = binding.emailField.text.toString()
        val password = binding.passwordField.text.toString()
        if (validateData(email, password)) {
            errorMessageHeader.visibility = View.GONE
            linearLoader.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "sign in User with email is successful")
                        FirebaseAuthentication.setCurrentUserUID(auth.currentUser?.uid)
                        errorMessageHeader.visibility = View.GONE
                        linearLoader.visibility = View.GONE
                        navController.navigate(R.id.action_loginFragment_to_homeScreenFragment)
                    } else {
                        when(task.exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                Log.d(TAG, "crate User with email is FAILURE", task.exception)
                                linearLoader.visibility = View.GONE
                                errorMessageHeader.visibility = View.VISIBLE
                            }
                            is FirebaseAuthException -> {
                                linearLoader.visibility = View.GONE
                                binding.errorHeaderMessage.text = getString(R.string.user_dont_exist)
                                errorMessageHeader.visibility = View.VISIBLE
                            }
                        }
                    }
                }
        }
    }

    private fun validateData(email: String, password: String): Boolean {
        val validEmail = isEmailValid(email)
        val validPassword = isPasswordValid(password)
        if (!validEmail) {
            binding.emailFieldLayout.error = "Please enter valid E-mail address"
        } else {
            binding.emailFieldLayout.isErrorEnabled = false
        }
        if (!validPassword) {
            binding.passwordFieldLayout.error = "Please enter valid password"
        } else {
            binding.passwordFieldLayout.isErrorEnabled = false
        }
        return validEmail && validPassword
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$"
        val pattern = Pattern.compile(emailRegex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isPasswordValid(password: String): Boolean {
        // The password must be at least 8 characters long.
        if (password.length < 8) {
            return false
        }

        /*// The password must contain at least one uppercase letter.
        val uppercaseRegex = Pattern.compile("[A-Z]")
        val uppercaseMatcher = uppercaseRegex.matcher(password)
        if (!uppercaseMatcher.find()) {
            return false
        }

        // The password must contain at least one lowercase letter.
        val lowercaseRegex = Pattern.compile("[a-z]")
        val lowercaseMatcher = lowercaseRegex.matcher(password)
        if (!lowercaseMatcher.find()) {
            return false
        }

        // The password must contain at least one number.
        val numberRegex = Pattern.compile("[0-9]")
        val numberMatcher = numberRegex.matcher(password)
        if (!numberMatcher.find()) {
            return false
        }

        // The password must contain at least one special character.
        val specialCharacterRegex = Pattern.compile("[!@#$%^&*()_+-]")
        val specialCharacterMatcher = specialCharacterRegex.matcher(password)
        if (!specialCharacterMatcher.find()) {
            return false
        }*/

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.myapplication.screens

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.model.FirebaseAuthentication

private const val TAG = "ProfileFragment"

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding!!


    private val auth = FirebaseAuthentication.getFirebaseInstance()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.emailText.text = auth.currentUser?.email.toString()

        binding.logOut.setOnClickListener {
            Log.d(TAG, "[+] Log out clicked")
            AlertDialog.Builder(requireContext())
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    auth.signOut()
                    navController.navigate(R.id.action_profileFragment_to_loginFragment)
                }
                .setNegativeButton("No") { _, _ ->

                }
                .show()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
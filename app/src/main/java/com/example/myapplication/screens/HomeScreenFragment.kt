package com.example.myapplication.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.myapplication.NUMBERS_OF_SHOPS_TO_FETCH
import com.example.myapplication.R
import com.example.myapplication.SHOPS_COLLECTION
import com.example.myapplication.adapter.ShopAdapter
import com.example.myapplication.databinding.FragmentHomeScreenBinding
import com.example.myapplication.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

private const val TAG = "HomeScreenFragment"

class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var adapter: ShopAdapter

    private lateinit var navController: NavController
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore: FirebaseFirestore = FireStoreObject.getFireStoreDatabaseInstance()
    private val viewModel: AppViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        //navigate to login fragment if user not logged in using firebase method
        if (auth.currentUser == null) {
            navController.navigate(R.id.action_homeScreenFragment_to_loginFragment)
        } else {
            binding.shopsItemShimmer.startShimmer()
            binding.productsItemShimmer.startShimmer()



            // Remove this after some time
//        viewModel.writeDataToProducts()
//        viewModel.createFakeShopUsers()

            binding.profileImg.setOnClickListener {
                navController.navigate(R.id.action_homeScreenFragment_to_profileFragment)
            }

            adapter = ShopAdapter {
                viewModel.setCurrentSelectedShop(it)
                navController.navigate(R.id.action_homeScreenFragment_to_orderScreen)
            }

            fetchShopsData()
            binding.shopsRecyclerView.adapter = adapter

        }

        return binding.root
    }

    private fun fetchShopsData() {
        val dataSet: MutableList<ShopUser> = mutableListOf()
        var productsItemList: List<String> = emptyList()
        firestore.collection(SHOPS_COLLECTION)
            .orderBy("shop.name")
            .limit(NUMBERS_OF_SHOPS_TO_FETCH)
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    dataSet.add(document.toObject<ShopUser>())
                }
                adapter.submitList(dataSet)
                binding.shopsItemShimmer.stopShimmer()
                binding.shopsItemShimmer.visibility = View.GONE
                binding.shopsRecyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting shops data", exception)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
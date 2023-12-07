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
import com.example.myapplication.R
import com.example.myapplication.adapter.ChooseOrderItemAdapter
import com.example.myapplication.adapter.SelectedOrderItemAdapter
import com.example.myapplication.databinding.FragmentOrderScreenBinding
import com.example.myapplication.model.AppViewModel
import com.example.myapplication.model.Order

private const val TAG = "OrderScreen"

class OrderScreen : Fragment() {

    private var _binding: FragmentOrderScreenBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var selectedItemAdapter: SelectedOrderItemAdapter
    private lateinit var chooseItemAdapter: ChooseOrderItemAdapter

    private val viewModel: AppViewModel by navGraphViewModels(R.id.nav_graph)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderScreenBinding.inflate(inflater, container, false)

        val shopUser = viewModel.getCurrentSelectedShop()

        selectedItemAdapter = SelectedOrderItemAdapter(
            onClick = {

            },
            onClickRemove = {
                viewModel.removeItemFromList(it)
                selectedItemAdapter.submitList(viewModel.getOrderList())
            }
        )
        binding.selectedOrderItemsRecyclerView.adapter = selectedItemAdapter

        chooseItemAdapter = ChooseOrderItemAdapter {
            if (viewModel.getOrderList().contains(Order(it, 0))){

            } else {
                viewModel.addItemToList(Order(it, 0))
                Log.d(TAG, "item: ${it} added")
                Log.d(TAG, "new list: ${viewModel.getOrderList()}")
                selectedItemAdapter.submitList(viewModel.getOrderList())
            }

        }
        binding.chooseOrderItemsRecyclerView.adapter = chooseItemAdapter
        chooseItemAdapter.submitList(shopUser!!.shop.products)

        binding.cancelButton.setOnClickListener {
            viewModel.setCurrentSelectedShop(null)
            navController.navigate(R.id.action_orderScreen_to_homeScreenFragment)
        }

        binding.requestOrderButton.setOnClickListener {

        }

//        fetchOrderItems()
        return binding.root
    }

//    private fun fetchOrderItems() {
//        Log.e(TAG, "name: ${shopUser!!.shop.name} list: ${shopUser!!.shop.products}")

//        chooseItemAdapter.submitList(shopUser!!.shop.products.map {
//            Order("", it, 0)
//        })
//        shopUser?.uid?.let {
//            firestore.collection(SHOPS_COLLECTION)
//                .document(it)
//                .get()
//                .addOnSuccessListener {
//                    shopUser = it.toObject<ShopUser>()
//                }
//                .addOnCompleteListener {
//                    Log.e(TAG, "name: ${shopUser!!.shop.name} list: ${shopUser!!.shop.products}")
//                    chooseItemAdapter.submitList(shopUser!!.shop.products.map {
//                        Order("", it, 0)
//                    })
//                    binding.loadingItemsLayout.visibility = View.GONE
//                    binding.chooseOrderItemsRecyclerView.visibility = View.VISIBLE
//                }
//                .addOnFailureListener { exception ->
//                    Log.e(TAG, "Error getting shops data", exception)
//                }
//        }


//    }

//    override fun onDetach() {
//        super.onDetach()
//        viewModel.emptyList()
//    }

}
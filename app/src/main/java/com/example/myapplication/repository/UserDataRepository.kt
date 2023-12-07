package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.PRODUCTS_COLLECTION
import com.example.myapplication.PRODUCTS_DOCUMENT
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductsItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

private const val TAG = "Repository"

class UserDataRepository(private val database: FirebaseFirestore) {

    fun addProductsToDatabase(productNamesList: List<String>) {
        database.collection(PRODUCTS_COLLECTION)
            .document(PRODUCTS_DOCUMENT)
            .set(
                hashMapOf(
                    "products_name_list" to productNamesList
                )
            )
            .addOnSuccessListener {
                Log.d(TAG, "Document data written for products is successful")
            }
            .addOnFailureListener {exception ->
                Log.e(TAG, "Error writting products data", exception)
            }
    }

    fun getProductsList(): List<String> {
        var productsNameList: List<String> = emptyList()

        database.collection(PRODUCTS_COLLECTION).document(PRODUCTS_DOCUMENT)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    productsNameList = documentSnapshot.get("products_name_list") as List<String>
                    Log.d(TAG, "Document data reading for products is successful : $productsNameList")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting products data", exception)
            }

        return productsNameList
    }
}
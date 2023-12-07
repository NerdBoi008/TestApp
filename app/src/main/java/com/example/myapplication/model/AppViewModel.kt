package com.example.myapplication.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductsItem
import com.example.myapplication.repository.UserDataRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

private const val TAG = "ViewModel" 
class AppViewModel(application: Application) : AndroidViewModel(application) {



    private val firestoreObj = FireStoreObject.getFireStoreDatabaseInstance()
    private val auth = FirebaseAuthentication.getFirebaseInstance()
    private var _currentSelectedShop: ShopUser? = null
    private var _orderList: Flow<MutableList<Order>> = flow {
        mutableListOf<Order>()
    }

    private val repository: UserDataRepository = UserDataRepository(firestoreObj)

    fun setCurrentSelectedShop(shopUser: ShopUser?) {
        _currentSelectedShop = shopUser
    }

    fun getCurrentSelectedShop(): ShopUser? = _currentSelectedShop

    fun addItemToList(order: Order) {
        viewModelScope.launch {
            _orderList.collect() {
                it.add(order)
            }
        }

    }

    fun removeItemFromList(order: Order) {
        viewModelScope.launch {
            _orderList.collect() {
                it.remove(order)
            }
        }
    }

    fun getOrderList(): MutableList<Order> = _orderList.collect()

    fun emptyList() {
        viewModelScope.launch {
            _orderList.collect() {
                emptyList()
            }
        }
    }

    class Factory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(AppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppViewModel(app) as T
            }
            throw IllegalAccessException("Unable to construct viewmodel")
        }
    }

}
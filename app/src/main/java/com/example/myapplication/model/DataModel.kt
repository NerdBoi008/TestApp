package com.example.myapplication.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//data class Shop(
//    val name: String,
//    val shopType: String,
//    val products: List<String>,
//    val orders: List<Order>? = null,
//    val address: Address
//)

//data class Address(
//    val building: String,
//    val area: String,
//    val city: String,
//    val street: String,
//    val state: String,
//    val zip: Int,
//)

data class Order(
    val productName: String,
    val quantity: Int
)

data class Product(
    val name: String
)

data class CustomerUser(
    val customerName: String,
    val customerEmail: String,
    val customerPhone: Int,
    val address: Address
)

//data class ShopUser(
//    val uid: String,
//    val shopUserName: String,
//    val shopUserEmail: String,
//    val shopUserPhone: Int,
//    val shop: Shop
//)

data class ShopUser(
    @get:PropertyName("uid") var uid: String,
    @get:PropertyName("shopUserName") val shopUserName: String,
    @get:PropertyName("shopUserEmail") val shopUserEmail: String,
    @get:PropertyName("shopUserPhone") val shopUserPhone: Int,
    @get:PropertyName("shop") val shop: Shop
) {
    constructor() :
            this(
                "",
                "",
                "",
                0,
                Shop(
                    "",
                    "",
                    emptyList(),
                    emptyList(),
                    Address(
                        "",
                        "",
                        "",
                        "",
                        "",
                        0)
                    )
                ) {
    }
}

data class Shop(
    @get:PropertyName("name") val name: String,
    @get:PropertyName("shopType") val shopType: String,
    @get:PropertyName("products") val products: List<String>,
    @get:PropertyName("orders") val orders: List<Order>? = null,
    @get:PropertyName("address") val address: Address
) {
    constructor(): this(
        "",
        "",
        emptyList(),
        emptyList(),
        Address(
        "",
        "",
        "",
        "",
        "",
        0)
        )
    {

    }
}

data class Address(
    @get:PropertyName("building") val building: String,
    @get:PropertyName("area") val area: String,
    @get:PropertyName("city") val city: String,
    @get:PropertyName("street") val street: String,
    @get:PropertyName("state") val state: String,
    @get:PropertyName("zip") val zip: Int,
) {
    constructor(): this("", "", "", "", "", 0) {

    }
}

enum class AccountType {
    CUSTOMER, SHOP
}

object CurrentUserData {
    var accountType: AccountType? = null

    private var shopUser: ShopUser? = null
    private var customerUser: CustomerUser? = null

    fun setCustomerUser(name: String, email: String, phone: Int, address: Address = Address("","","","","",0)) {
        customerUser = CustomerUser(name, email, phone, address)
    }

    fun getCustomerUser(): CustomerUser? {
        return customerUser
    }

    fun setShopUser(name: String, email: String, phone: Int, shop: Shop = Shop("","", emptyList(), emptyList(),Address("","","","","",0))) {
        shopUser = ShopUser("", name, email, phone, shop)
    }

    fun getShopUser(): ShopUser? {
        return shopUser
    }
}

object FirebaseAuthentication {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUserUID: String? = null

    fun getFirebaseInstance(): FirebaseAuth {
        return auth
    }

    fun setCurrentUserUID(uid: String?): Boolean {
        if (uid?.isNotEmpty() == true) {
            currentUserUID = uid
            Log.d("LoginFragment", "UID obtained ${currentUserUID}")
            return true
        }
        return false
    }

    fun getCurrentUserUID(): String? {
        return currentUserUID
    }
}

object FireStoreObject {
    private val db = Firebase.firestore

    fun getFireStoreDatabaseInstance(): FirebaseFirestore {
        return db
    }

}
package com.example.myapplication.screens

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductListItem
import com.example.myapplication.adapter.ProductsListAdapter
import com.example.myapplication.databinding.AddressLayoutBinding
import com.example.myapplication.databinding.FragmentSignupBinding
import com.example.myapplication.databinding.PersonalDetailsLayoutBinding
import com.example.myapplication.databinding.ProductsLayoutBinding
import com.example.myapplication.model.*
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

private const val TAG = "SignupFragment"

class SignupFragment: Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding
        get() = _binding!!

    private val currentUserData = CurrentUserData
    private val auth = FirebaseAuthentication.getFirebaseInstance()
    private val firestoreDatabase = FireStoreObject.getFireStoreDatabaseInstance()
    private lateinit var navController: NavController
    private lateinit var password: String

    private lateinit var linearLoader: LinearProgressIndicator
    private lateinit var personalDetailsLayout: PersonalDetailsLayoutBinding
    private lateinit var addressDetailsLayout: AddressLayoutBinding
    private lateinit var productsDetailsLayout: ProductsLayoutBinding


    private val mutableList = mutableListOf<ProductListItem>()
    private val selectedItems = mutableListOf<ProductListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        linearLoader = binding.linearLoader
        personalDetailsLayout = binding.personalLayout
        addressDetailsLayout = binding.addressLayout
        productsDetailsLayout = binding.productsLayout

         lifecycleScope.launch {
             withContext(Dispatchers.IO) {
                 for(index in 1..20) {
                     mutableList.add(ProductListItem("item$index", CheckBox(requireContext())))
                 }

                 binding.productsLayout.productListRecyclerView.adapter = ProductsListAdapter(
                     dataSet = mutableList.toList(),
                     onChecked = {
                        selectedItems.add(it)
                     },
                     onUnChecked = {
                         selectedItems.remove(it)
                     }
                 )
             }
         }

        binding.customerCardView.setOnClickListener {
            navigateLayout(binding.screenLabel.text.toString(), true)
            binding.headerLayout.visibility = View.VISIBLE
            binding.bottomButtomLayout.visibility = View.VISIBLE
            currentUserData.accountType = AccountType.CUSTOMER

        }

        binding.shopCardView.setOnClickListener {
            navigateLayout(binding.screenLabel.text.toString(), true)
            binding.headerLayout.visibility = View.VISIBLE
            binding.bottomButtomLayout.visibility = View.VISIBLE
            currentUserData.accountType = AccountType.SHOP

        }

        binding.next.setOnClickListener {
            navigateLayout(binding.screenLabel.text.toString(), true)
        }

        binding.back.setOnClickListener {
            navigateLayout(binding.screenLabel.text.toString(), false)
        }


//        binding.signupButton.setOnClickListener {
//            signUpUser()
//        }

        return binding.root
    }


    private fun navigateLayout(currentScreen: String, navDirection: Boolean) {
        when(currentScreen) {
            getString(R.string.sign_up) -> {
                if (navDirection) {
                    binding.screenLabel.text = getString(R.string.personal_details)
                    animateForwardNavigation(binding.personalLayout.root, binding.chooseAccountLayout, 250L)
                }
            }
            getString(R.string.personal_details) -> {

                val username = personalDetailsLayout.signupUserNameEditText.text.toString()
                val email = personalDetailsLayout.emailEditText.text.toString()
                password = personalDetailsLayout.passwordFieldEditText.text.toString()
                val mobileNumber = personalDetailsLayout.mobileNumber.text.toString()

                if (validatePersonalDetailsData(username, email, password, mobileNumber)) {

                    if(navDirection && (currentUserData.accountType == AccountType.SHOP)) {

                        currentUserData.setShopUser(username, email, mobileNumber.toInt())

                        binding.screenLabel.text = getString(R.string.shop_details)
                        binding.addressLayout.shopLayout.visibility = View.VISIBLE
                        animateForwardNavigation(binding.addressLayout.root, binding.personalLayout.root, 250L)
                    }
                    if (navDirection && (currentUserData.accountType == AccountType.CUSTOMER)) {

                        currentUserData.setCustomerUser(username, email, mobileNumber.toInt())

                        binding.addressLayout.shopLayout.visibility = View.GONE
                        binding.screenLabel.text = getString(R.string.address_details)
                        animateForwardNavigation(binding.addressLayout.root, binding.personalLayout.root, 250L)
                        binding.next.text = getString(R.string.sign_up)
                    }
                }
//                if(navDirection && (currentUserData.accountType == AccountType.SHOP)) {
//                    binding.screenLabel.text = getString(R.string.shop_details)
//                    binding.addressLayout.shopLayout.visibility = View.VISIBLE
//                    animateForwardNavigation(binding.addressLayout.root, binding.personalLayout.root, 250L)
//                }
//                if (navDirection && (currentUserData.accountType == AccountType.CUSTOMER)) {
//                    binding.addressLayout.shopLayout.visibility = View.GONE
//                    binding.screenLabel.text = getString(R.string.address_details)
//                    animateForwardNavigation(binding.addressLayout.root, binding.personalLayout.root, 250L)
//                    binding.next.text = getString(R.string.sign_up)
//                }
                if (!navDirection) {
                    binding.headerLayout.visibility = View.GONE
                    binding.bottomButtomLayout.visibility = View.GONE
                    binding.screenLabel.text = getString(R.string.sign_up)
                    animateBackwardNavigation(binding.chooseAccountLayout, binding.personalLayout.root, 250L)
                }

            }
            getString(R.string.address_details), getString(R.string.shop_details) -> {

                val building = addressDetailsLayout.buildingEditText.text.toString()
                val area = addressDetailsLayout.areaEditText.text.toString()
                val street = addressDetailsLayout.streetEditText.text.toString()
                val city = addressDetailsLayout.cityEditText.text.toString()
                val state = addressDetailsLayout.stateEditText.text.toString()
                val zip = addressDetailsLayout.zipEditText.text.toString()
                val shopName = addressDetailsLayout.shopNameEditText.text.toString()
                val shopType = addressDetailsLayout.shopTypeEditText.text.toString()

                val shopUser = currentUserData.getShopUser()
                val customerUser = currentUserData.getCustomerUser()

                if (validateAddressDetailsData(building, area, street, city, state, zip)) {
                    if (navDirection && (currentUserData.accountType == AccountType.SHOP)) {
                        if (validateShopDetailsData(shopName, shopType)) {
                            binding.next.text = getString(R.string.sign_up)
                            binding.screenLabel.text = getString(R.string.product_details)

                            if (shopUser != null) {
                                currentUserData.setShopUser(
                                    shopUser.shopUserName,
                                    shopUser.shopUserEmail,
                                    shopUser.shopUserPhone,
                                    Shop(
                                        name = shopName,
                                        shopType = shopType,
                                        products = emptyList(),
                                        address = Address(
                                            building = building,
                                            area = area,
                                            street = street,
                                            city = city,
                                            state = state,
                                            zip = zip.toInt()
                                        )
                                    )
                                )
                            }
    //                        Log.d(TAG, "shop name: ${currentUserData.getShopUser()!!.name} \n${currentUserData.getShopUser()!!.shop.address.zip} \nProducts: ${currentUserData.getShopUser()!!.shop.products}")
                            animateForwardNavigation(binding.productsLayout.root, binding.addressLayout.root, 250L)
                        }
                    }
                    if (navDirection && (currentUserData.accountType == AccountType.CUSTOMER)) {
                        if (customerUser != null) {
                            currentUserData.setCustomerUser(customerUser.customerName, customerUser.customerEmail, customerUser.customerPhone, Address(building, area, street, city, state, zip.toInt()))
                        }

                        currentUserData.getCustomerUser()?.let { customerSignIn(it) }

                        Log.d(TAG, "sign in call \ncustomer name: ${currentUserData.getCustomerUser()!!.customerName} \naddress: ${currentUserData.getCustomerUser()!!.address.area}")
                    }
                }
//                if (navDirection && (currentUserData.accountType == AccountType.SHOP)) {
//                    binding.next.text = getString(R.string.sign_up)
//                    binding.screenLabel.text = getString(R.string.product_details)
//
//                    animateForwardNavigation(binding.productsLayout.root, binding.addressLayout.root, 250L)
//                }
//                if (navDirection && (currentUserData.accountType == AccountType.CUSTOMER)) {
////                    currentUserData.setCustomerUser(user.name, user.email, user.phone, Address(building, area, street, city, state, zip.toInt()))
//                }
                if(!navDirection) {
                    binding.next.text = getString(R.string.next)
                    binding.screenLabel.text = getString(R.string.personal_details)
                    animateBackwardNavigation(binding.personalLayout.root, binding.addressLayout.root, 250L)
                }

            }
            getString(R.string.product_details) -> {
                val user = currentUserData.getShopUser()!!

                if (navDirection) {
//                    val selectedItems = mutableListOf<String>()
//                    for (item in mutableList) {
////                        item.checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
//                            if (item.checkBox.isChecked) {
//                                Log.d(TAG, "[+] ${item.productName} is added")
//                                selectedItems.add(item.productName)
//                            }
////                        }
//                    }

                    currentUserData.setShopUser(
                        user.shopUserName,
                        user.shopUserEmail,
                        user.shopUserPhone,
                        Shop(
                            name = user.shop.name,
                            shopType = user.shop.shopType,
                            products = selectedItems.map {
                                it.productName
                            },
                            address = Address(
                                building = user.shop.address.building,
                                area = user.shop.address.area,
                                street = user.shop.address.street,
                                city = user.shop.address.city,
                                state = user.shop.address.state,
                                zip = user.shop.address.zip
                            )
                        )
                    )

                    Log.d(TAG, "shop name: ${currentUserData.getShopUser()!!.shopUserName} \n${currentUserData.getShopUser()!!.shop.address.zip} \nProducts: ${currentUserData.getShopUser()!!.shop.products}")
                    Log.d(TAG, "sing in call \n selected items ${selectedItems.map { it.productName }}")
                    currentUserData.getShopUser()?.let {
                        shopSignIn(it)
                    }
                }
                if(!navDirection) {
                    binding.next.text = getString(R.string.next)
                    binding.screenLabel.text = getString(R.string.shop_details)
                    animateBackwardNavigation(binding.addressLayout.root, binding.productsLayout.root, 250L)
                }
            }
        }
    }

    private fun customerSignIn(customerUser: CustomerUser) {
        linearLoader.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(customerUser.customerEmail, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "sign in User with email is successful")
//                        runBlocking {
//                            UserLoginDataStore(requireContext()).saveUserLoginDataToDataStore(email, password, requireContext())
//                        }
                    FirebaseAuthentication.setCurrentUserUID(auth.currentUser?.uid)
                    firestoreDatabase.collection("customers")
                        .document(FirebaseAuthentication.getCurrentUserUID()!!)
                        .set(
                            customerUser
                        )
                        .addOnSuccessListener {
                            Log.d(TAG, "Document data written for user is successful")
                        }
                        .addOnFailureListener {exception ->
                            Log.e(TAG, "Error writing data", exception)
                        }
                    linearLoader.visibility = View.GONE
                    findNavController().navigate(R.id.action_signupFragment_to_homeScreenFragment)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d(TAG, "crate User with email is FAILURE", task.exception)
                        linearLoader.visibility = View.GONE
                    }
                }
            }
    }

    private fun shopSignIn(shopUser: ShopUser) {
        linearLoader.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(shopUser.shopUserEmail, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "sign in User with email is successful")
//                        runBlocking {
//                            UserLoginDataStore(requireContext()).saveUserLoginDataToDataStore(email, password, requireContext())
//                        }
                    FirebaseAuthentication.setCurrentUserUID(auth.currentUser?.uid)
                    shopUser.uid = FirebaseAuthentication.getCurrentUserUID()!!
                    firestoreDatabase.collection("shops")
                        .document(FirebaseAuthentication.getCurrentUserUID()!!)
                        .set(
                            shopUser
                        )
                        .addOnCompleteListener {

                        }
                        .addOnSuccessListener {
                            Log.d(TAG, "Document data written for user is successful")
                        }
                        .addOnFailureListener {exception ->
                            Log.e(TAG, "Error writing data", exception)
                        }
                    linearLoader.visibility = View.GONE
                    findNavController().navigate(R.id.action_signupFragment_to_homeScreenFragment)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d(TAG, "crate User with email is FAILURE", task.exception)
                        linearLoader.visibility = View.GONE
                    }
                }
            }
    }

//    private fun signUpUser() {
//        val username = binding.signupUserNameEditText.text.toString()
//        val email = binding.emailEditText.text.toString()
//        val password = binding.passwordFieldEditText.text.toString()
//        val mobileNumber = binding.mobileNumber.text.toString()
//
//        if (validateData(username, email, password, mobileNumber)) {
//            linearLoader.visibility = View.VISIBLE
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener {task ->
//                    if (task.isSuccessful) {
//                        Log.d(TAG, "sign in User with email is successful")
////                        runBlocking {
////                            UserLoginDataStore(requireContext()).saveUserLoginDataToDataStore(email, password, requireContext())
////                        }
//                        FirebaseAuthentication.setCurrentUserUID(auth.currentUser?.uid)
//                        firestoreDatabase.collection("users")
//                            .document(FirebaseAuthentication.getCurrentUserUID()!!)
//                            .set(
//                                hashMapOf(
//                                    "name" to username,
//                                    "mobile" to mobileNumber,
//                                    "account_type" to binding.accountTypeDropDownMenuOptions.text.toString()
//                                )
//                            )
//                            .addOnSuccessListener {
//                                Log.d(TAG, "Document data written for user is successful")
//                            }
//                            .addOnFailureListener {exception ->
//                                Log.e(TAG, "Error writing data", exception)
//                            }
//                        linearLoader.visibility = View.GONE
//                        findNavController().navigate(R.id.action_signupFragment_to_homeScreenFragment)
//                    } else {
//                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                            Log.d(TAG, "crate User with email is FAILURE", task.exception)
//                            linearLoader.visibility = View.GONE
//                        }
//                    }
//            }
//        }
//    }

    private fun validatePersonalDetailsData(username: String, email: String, password: String, mobileNumber: String): Boolean {
        val validUserName = isUsernameValid(username)
        val validEmail = isEmailValid(email)
        val validPassword = isPasswordValid(password)
        val confirmPassword = personalDetailsLayout.confirmPasswordFieldEditText.text.toString()
        val validMobileNumber = isMobileNumberValid(mobileNumber)

        if (!validUserName) {
            personalDetailsLayout.userNameLayout.error = "Please enter valid user name"
        } else {
            personalDetailsLayout.userNameLayout.isErrorEnabled = false
        }
        if (!validEmail) {
            personalDetailsLayout.emailLayout.error = "Please enter valid E-mail address"
        } else {
            personalDetailsLayout.emailLayout.isErrorEnabled = false
        }
        if (!validPassword) {
            personalDetailsLayout.passwordLayout.error = "Please enter valid password"
        } else {
            personalDetailsLayout.passwordLayout.isErrorEnabled = false
        }
        if (password != confirmPassword) {
            personalDetailsLayout.confirmPasswordLayout.error = "Confirm password does not match"
        } else {
            personalDetailsLayout.confirmPasswordLayout.isErrorEnabled = false
        }
        if (!validMobileNumber) {
            personalDetailsLayout.mobileLayout.error = "Please enter valid mobile number"
        } else {
            personalDetailsLayout.mobileLayout.isErrorEnabled = false
        }
        return validUserName && validEmail && validPassword && validMobileNumber
    }

    private fun validateShopDetailsData(shopName: String, shopType: String): Boolean {
        val isShopName = shopName.isNotEmpty()
        val isShopType = shopType.isNotEmpty()

        if (!isShopName) {
            addressDetailsLayout.shopNameLayout.error = "Please enter valid shop name"
        } else {
            addressDetailsLayout.shopNameLayout.isErrorEnabled = false
        }
        if (!isShopType) {
            addressDetailsLayout.shopTypeLayout.error = "Please enter valid shop type"
        } else {
            addressDetailsLayout.shopTypeLayout.isErrorEnabled = false
        }

        return isShopName && isShopType
    }

    private fun validateAddressDetailsData(building: String, area: String, street: String, city: String, state: String, zipCode: String): Boolean {
        val isBuilding = building.isNotEmpty()
        val isArea = area.isNotEmpty()
        val isStreet = street.isNotEmpty()
        val isCity = city.isNotEmpty()
        val isState = state.isNotEmpty()
        var isZipCode = zipCode.isNotEmpty()

        if (!isBuilding) {
            addressDetailsLayout.buildingLayout.error = "Please enter valid building name"
        } else {
            addressDetailsLayout.buildingLayout.isErrorEnabled = false
        }
        if (!isArea) {
            addressDetailsLayout.areaLayout.error = "Please enter valid area name"
        } else {
            addressDetailsLayout.areaLayout.isErrorEnabled = false
        }
        if (!isStreet) {
            addressDetailsLayout.streetLayout.error = "Please enter valid street name"
        } else {
            addressDetailsLayout.streetLayout.isErrorEnabled = false
        }
        if (!isCity) {
            addressDetailsLayout.cityLayout.error = "Please enter valid city name"
        } else {
            addressDetailsLayout.cityLayout.isErrorEnabled = false
        }
        if (!isState) {
            addressDetailsLayout.stateLayout.error = "Please enter valid state name"
        } else {
            addressDetailsLayout.stateLayout.isErrorEnabled = false
        }
//        if (!isZipCode && !(zipCode.length == 6)) {
        if (zipCode.length != 6) {
            isZipCode = false
            addressDetailsLayout.zipLayout.error = "Please enter valid zip code"
        } else {
            addressDetailsLayout.zipLayout.isErrorEnabled = false
        }
        return isBuilding && isArea && isStreet && isCity && isState && isZipCode
    }

    private fun isUsernameValid(username: String): Boolean {
//        val regex = "^[A-Za-z][A-Za-z0-9_]{7,29}$"
//        return Regex(regex).matches(username)

        return (username.length > 3)
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$"
        val pattern = Pattern.compile(emailRegex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun isPasswordValid(password: String): Boolean {
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

    private fun isMobileNumberValid(mobileNumber: String): Boolean {
        val regex = "^[+]?[0-9]{10,13}$"
        return Regex(regex).matches(mobileNumber)
    }

    private fun animateForwardNavigation(enteringLayout: ViewGroup, exitingLayout: ViewGroup, duration: Long) {
        val fadeIn = ObjectAnimator.ofFloat(enteringLayout, "alpha", 0f, 1f)
        fadeIn.duration = duration
        fadeIn.start()

        val slideIn = ObjectAnimator.ofFloat(enteringLayout, "translationX", 200f, 0f)
        slideIn.duration = duration
        slideIn.start()

        enteringLayout.visibility = View.VISIBLE

        val fadeOut = ObjectAnimator.ofFloat(exitingLayout, "alpha", 1f, 0f)
        fadeOut.duration = duration
        fadeOut.start()

        val slideOut = ObjectAnimator.ofFloat(exitingLayout, "translationX", 0f, -200f)
        slideOut.duration = duration
        slideOut.start()

        exitingLayout.visibility = View.GONE
    }

    private fun animateBackwardNavigation( enteringLayout: ViewGroup, exitingLayout: ViewGroup, duration: Long) {
        val fadeIn = ObjectAnimator.ofFloat(enteringLayout, "alpha", 0f, 1f)
        fadeIn.duration = duration
        fadeIn.start()

        val slideIn = ObjectAnimator.ofFloat(enteringLayout, "translationX", -200f, 0f)
        slideIn.duration = duration
        slideIn.start()

        enteringLayout.visibility = View.VISIBLE

        val fadeOut = ObjectAnimator.ofFloat(exitingLayout, "alpha", 1f, 0f)
        fadeOut.duration = duration
        fadeOut.start()

        val slideOut = ObjectAnimator.ofFloat(exitingLayout, "translationX", 0f, 200f)
        slideOut.duration = duration
        slideOut.start()

        exitingLayout.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
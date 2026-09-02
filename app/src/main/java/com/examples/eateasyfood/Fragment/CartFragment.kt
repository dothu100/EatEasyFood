package com.examples.eateasyfood.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eateasyfood.databinding.FragmentCartBinding
import com.examples.eateasyfood.PayOutActivity
import com.examples.eateasyfood.adaptar.CartAdapter
import com.examples.eateasyfood.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodImageUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        retrieveCartItems()

        binding.proceedButton.setOnClickListener {
            getOrderItemsDetail()
        }

        return binding.root
    }

    private fun getOrderItemsDetail() {
        userId = auth.currentUser?.uid ?: ""
        val orderIdReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")

        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()
        val foodQuantity = cartAdapter.getUpdatedItemsQuatity()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children ) {
                    val orderItems = foodSnapshot.getValue(CartItems:: class.java)

                    orderItems?.foodName?.let {
                        foodName.add(it)
                    }
                    orderItems?.foodPrice?.let {
                        foodPrice.add(it)
                    }
                    orderItems?.foodDescription?.let {
                        foodDescription.add(it)
                    }
                    orderItems?.foodImage?.let {
                        foodImage.add(it)
                    }
                    orderItems?.foodIngredient?.let {
                        foodIngredient.add(it)
                    }
                }
                orderNow(
                    foodName,
                    foodPrice,
                    foodDescription,
                    foodImage,
                    foodIngredient,
                    foodQuantity
                )

                // Reset giỏ hàng sau khi đặt hàng
                resetCart()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Order making failed. Please try again", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantitys: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("FoodItemName", ArrayList(foodName))
            intent.putExtra("FoodItemPrice", ArrayList(foodPrice))
            intent.putExtra("FoodItemImage", ArrayList(foodImage))
            intent.putExtra("FoodItemDescription", ArrayList(foodDescription))
            intent.putExtra("FoodItemIngredient", ArrayList(foodIngredient))
            intent.putExtra("FoodItemQuantitys", ArrayList(foodQuantitys))
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        userId = auth.currentUser?.uid ?: ""
        val foodReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")

        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodImageUri = mutableListOf()
        foodIngredients = mutableListOf()
        quantity = mutableListOf()

        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)

                    cartItems?.foodName?.let {
                        foodNames.add(it)
                    }
                    cartItems?.foodPrice?.let {
                        foodPrices.add(it)
                    }
                    cartItems?.foodDescription?.let {
                        foodDescriptions.add(it)
                    }
                    cartItems?.foodImage?.let {
                        foodImageUri.add(it)
                    }
                    cartItems?.foodIngredient?.let {
                        foodIngredients.add(it)
                    }
                    cartItems?.foodQuantity?.let {
                        quantity.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Data not fetched", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter() {
        cartAdapter = CartAdapter(
            requireContext(),
            foodNames,
            foodPrices,
            foodDescriptions,
            foodImageUri,
            quantity,
            foodIngredients
        )
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.cartRecyclerView.adapter = cartAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetCart() {
        val cartReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")
        cartReference.removeValue()
        foodNames.clear()
        foodPrices.clear()
        foodDescriptions.clear()
        foodImageUri.clear()
        foodIngredients.clear()
        quantity.clear()
        cartAdapter.notifyDataSetChanged()
    }
}

package com.example.thriftwears

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.adapter.CartAdapter
import com.example.thriftwears.databinding.ActivityCartBinding
import com.example.thriftwears.viewmodel.GlobalCartViewModel
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.thriftwears.item.CartItem
import java.util.ArrayList

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private var currentFragmentTag: String? = null
    private var currentState: String? = null
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var cartAdapter: CartAdapter
    private val globalCartViewModel : GlobalCartViewModel by viewModels()

    companion object {
        private const val MODEL_KEY = "MODEL_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityCartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.primary)

        currentState = savedInstanceState?.getString(MODEL_KEY)
        if (currentState == null) {
            initializeUI()
        }

        val cartData = intent.getParcelableArrayListExtra<CartItem> ("cart_data")

        if (cartData!!.isNotEmpty()) {
            binding.emptyCart.visibility = View.GONE

            for (item in cartData) {
                globalCartViewModel.addItem(item)
            }

            cartRecyclerView = findViewById(R.id.cartRecyclerView)
            totalPriceTextView = findViewById(R.id.totalPriceTextView)

            globalCartViewModel.items.observe(this, Observer { cartItems ->

                cartAdapter = CartAdapter(cartItems) { _, _ ->
                    updateTotalPrice(cartItems)
                    Log.d("CART", "Adding item to cart: $cartItems")
                }

                cartRecyclerView.adapter = cartAdapter
                cartRecyclerView.layoutManager = LinearLayoutManager(this)

                updateTotalPrice(cartItems)
            })
        } else {
            binding.emptyCart.visibility = View.VISIBLE
        }

    }

    private fun initializeUI() {
        binding.buyNowButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)

        binding.backToLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            val cartData = globalCartViewModel.items.value
            intent.putParcelableArrayListExtra("cart_data", cartData?.let { it1 -> ArrayList(it1) })
            this.startActivity(intent)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentFragmentTag)
    }

    @SuppressLint("DefaultLocale")
    private fun updateTotalPrice( cartItems: MutableList<CartItem>) {
        val totalPrice = cartItems.sumOf { it.price * it.quantity }
        getString(R.string.total, String.format("%.2f", totalPrice)).also { totalPriceTextView.text = it }
    }

}
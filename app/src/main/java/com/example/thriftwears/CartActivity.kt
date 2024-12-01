package com.example.thriftwears

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.example.thriftwears.item.CartItem
import com.example.thriftwears.viewmodel.GlobalCart

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private var currentFragmentTag: String? = null
    private var currentState: String? = null
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var cartAdapter: CartAdapter
    private val globalCartViewModel = GlobalCart()

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
    }

    private fun initializeUI() {
        binding.buyNowButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)

        binding.backToLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)

        // Sample data
        globalCartViewModel.addItem(CartItem("1", "Item 1", 10.0, 1))
        globalCartViewModel.addItem(CartItem("2", "Item 2", 15.0, 2))

        cartAdapter = CartAdapter(globalCartViewModel.items.value!!) { _, _ ->
            updateTotalPrice()
        }

        cartRecyclerView.adapter = cartAdapter
        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        updateTotalPrice()

        if(globalCartViewModel.items.value!!.isEmpty()){
            binding.emptyCart.visibility = View.VISIBLE
        } else {
            binding.emptyCart.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentFragmentTag)
    }

    @SuppressLint("DefaultLocale")
    private fun updateTotalPrice() {
        val totalPrice = globalCartViewModel.items.value!!.sumOf { it.price * it.quantity }
        getString(R.string.total, String.format("%.2f", totalPrice)).also { totalPriceTextView.text = it }
    }

}
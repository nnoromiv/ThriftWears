package com.nnorom.thriftwears

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nnorom.thriftwears.adapter.CartAdapter
import com.nnorom.thriftwears.databinding.ActivityCartBinding
import com.nnorom.thriftwears.viewmodel.GlobalCartViewModel
import androidx.activity.viewModels
import com.nnorom.thriftwears.item.Amount
import com.nnorom.thriftwears.item.CartItem
import com.nnorom.thriftwears.item.ExperienceContext
import com.nnorom.thriftwears.item.PayPalAuth
import com.nnorom.thriftwears.item.PayPalOrderResponse
import com.nnorom.thriftwears.item.PaymentSource
import com.nnorom.thriftwears.item.Paypal
import com.nnorom.thriftwears.item.PaypalOrderRequest
import com.nnorom.thriftwears.item.PurchaseUnit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.gson.Gson
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.Environment
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import kotlin.collections.ArrayList

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private var currentFragmentTag: String? = null
    private var currentState: String? = null
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var cartAdapter: CartAdapter
    private val globalCartViewModel : GlobalCartViewModel by viewModels()
    private val clientId = "AVH5DsjO-L0lX084MFJdPM18INQ8eqXz8GXm8RHv43TREqRIejjSUsZS-3O9PWp0I2yHUm32Y6CLU4Rx"
    private val clientSecret = "EOaRvQS91CceYEtBnB9To4Du7CiSfL9Ojjxtpt0vSB7zV3Uepl5rXpbkFV2RZ-R39KrjIv_Zi8emwKlr"
    private lateinit var auth: FirebaseAuth
    private var payPalAuth = PayPalAuth()
    private var payPalOrder = PayPalOrderResponse()
    private val returnUrl = "https://app://thriftwears/home"

    companion object {
        private const val MODEL_KEY = "MODEL_KEY"
        private const val TAG = "CART"
    }


    override fun onStart() {
        super.onStart()
        auth = com.google.firebase.ktx.Firebase.auth // Initialize FirebaseAuth instance

        val currentUser = auth.currentUser
        if (currentUser == null) {
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityCartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        window.statusBarColor = getColor(R.color.primary)

        currentState = savedInstanceState?.getString(MODEL_KEY)
        if (currentState == null) {
            initializeUI()
        }

        val cartData : ArrayList<CartItem>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cart_data", CartItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("cart_data")
        }

        if (cartData!!.isNotEmpty()) {
            authenticatePaypal(clientId, clientSecret)
            binding.emptyCart.visibility = View.GONE

            for (item in cartData) {
                globalCartViewModel.addItem(item)
            }

            cartRecyclerView = findViewById(R.id.cartRecyclerView)
            totalPriceTextView = findViewById(R.id.totalPriceTextView)

            globalCartViewModel.items.observe(this) { cartItems ->

                cartAdapter = CartAdapter(cartItems) { _, _ ->
                    updateTotalPrice(cartItems)
                    Log.d("CART", "Adding item to cart: $cartItems")
                }

                cartRecyclerView.adapter = cartAdapter
                cartRecyclerView.layoutManager = LinearLayoutManager(this)

                updateTotalPrice(cartItems)
            }
        } else {
            binding.emptyCart.visibility = View.VISIBLE
        }

    }

    private fun updateUI() {
        // Navigate to LoginActivity and finish the parent activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initializeUI() {

        binding.backToLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            val cartData = globalCartViewModel.items.value
            intent.putParcelableArrayListExtra("cart_data", cartData?.let { it1 -> ArrayList(it1) })
            this.startActivity(intent)
        }

        binding.paypalButton.setOnClickListener {
            createPayPalOrder()
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

    private fun authenticatePaypal(clientId: String = this.clientId, clientSecret: String = this.clientSecret) {
        val client = OkHttpClient()

        val credentials = "$clientId:$clientSecret"
        val authHeader = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val formBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .build()

        val request = Request.Builder()
            .url("https://api-m.sandbox.paypal.com/v1/oauth2/token")
            .post(formBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", authHeader)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    payPalAuth = Gson().fromJson(responseBody, PayPalAuth::class.java)
                    Log.d(TAG, "Response: $payPalAuth")
                } else {
                    Log.d(TAG, "Error: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Error: ${e.message}")
            }
        })

    }

    private fun createPayPalOrder() {
        if(payPalAuth.app_id!!.isNotEmpty()){
            val config = CoreConfig(clientId, environment = Environment.SANDBOX)
            val payPalWebCheckoutClient = PayPalWebCheckoutClient(this, config, returnUrl)

            val client = OkHttpClient()
            val referenceId = generateRandomId()

            val amount = Amount(currency_code = "GBP", value = "10.00")
            val purchaseUnit = PurchaseUnit(reference_id = referenceId, amount = amount)
            val experienceContext = ExperienceContext(
                paymentMethodPreference = "IMMEDIATE_PAYMENT_REQUIRED",
                brandName = "THRIFTWEARS INC",
                locale = "en-US",
                landingPage = "LOGIN",
                userAction = "PAY_NOW",
                returnUrl = returnUrl,
                cancelUrl = returnUrl
            )
            val paypal = Paypal(experience_context = experienceContext)
            val paymentSource = PaymentSource(paypal = paypal)
            val paypalOrderRequest = PaypalOrderRequest(
                intent = "CAPTURE",
                purchase_units = listOf(purchaseUnit),
                payment_source = paymentSource
            )

            val jsonBody = Gson().toJson(paypalOrderRequest)

            val body = jsonBody
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v2/checkout/orders")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("PayPal-Request-Id", auth.currentUser!!.uid)
                .addHeader("Authorization", "${payPalAuth.token_type} ${payPalAuth.access_token}")
                .build()

            Log.d(TAG, "Body: $jsonBody")

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        payPalOrder = Gson().fromJson(responseBody, PayPalOrderResponse::class.java)

                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.cart_activity, Payment(payPalOrder.id!!, payPalWebCheckoutClient), this::class.java.simpleName)
                            commit()
                        }

                        Log.d(TAG, "Response: $payPalOrder")
                    } else {
                        Log.d(TAG, "Response Error: ${response.message}")
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "Error: ${e.message}")
                }
            })
        } else {
            authenticatePaypal()
        }
    }

    private fun generateRandomId(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }

}
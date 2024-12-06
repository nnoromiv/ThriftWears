package com.nnorom.thriftwears

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nnorom.thriftwears.databinding.ActivityPaymentBinding
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest

class Payment(
    private val orderId: String,
    private val payPalWebCheckoutClient : PayPalWebCheckoutClient
) : Fragment() {

    private var _binding: ActivityPaymentBinding? = null
    private val binding get() = _binding!!

    private var currentState: String? = null

    companion object {
        private const val MODEL_KEY = "ITEM_MODEL_KEY"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore previous state or set initial UI state
        currentState = savedInstanceState?.getString(MODEL_KEY)
        if (currentState == null) {
            initializeUI()
        }

    }

    private fun initializeUI() {
        // Set up UI elements

        binding.open.visibility = View.GONE

        val payPalWebCheckoutRequest = PayPalWebCheckoutRequest(orderId, fundingSource = PayPalWebCheckoutFundingSource.PAYPAL)

        payPalWebCheckoutClient.start(
            requireActivity(),
            payPalWebCheckoutRequest
        )

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}

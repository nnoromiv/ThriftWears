package com.nnorom.thriftwears.item
import com.google.gson.annotations.SerializedName

data class PayPalAuth (
    var scope: String? = null,
    var access_token: String? = null,
    var token_type: String? = "Bearer",
    var app_id: String? = null,
    var expires_in: Int? = 0,
    var nonce: String? = null
)

data class PaypalOrderRequest(
    val intent: String,
    val purchase_units: List<PurchaseUnit>,
    val payment_source: PaymentSource
)

data class PurchaseUnit(
    val reference_id: String,
    val amount: Amount,
    val payee: PayPalPayee? = null
)

data class Amount(
    val currency_code: String,
    val value: String
)

data class PaymentSource(
    val paypal: Paypal
)

data class Paypal(
    val experience_context: ExperienceContext
)

data class ExperienceContext(
    @SerializedName("payment_method_preference")
    val paymentMethodPreference: String,
    @SerializedName("brand_name")
    val brandName: String,
    val locale: String,
    @SerializedName("landing_page")
    val landingPage: String,
    @SerializedName("user_action")
    val userAction: String,
    @SerializedName("return_url")
    val returnUrl: String,
    @SerializedName("cancel_url")
    val cancelUrl: String
)

data class PayPalPayee (
    val email_address: String,
    @SerializedName("merchant_id")
    val merchantId: String
)

data class Link(
    val href: String,
    val rel: String,
    val method: String
)

data class PayPalOrderResponse(
    val id: String? = null,
    val intent: String? = null,
    val status: String? = null,
    val purchase_units: List<PurchaseUnit>? = null,
    val create_time: String? = null,
    val links: List<Link>? = null
)

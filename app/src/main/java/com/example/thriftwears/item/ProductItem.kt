package com.example.thriftwears.item

import com.google.firebase.Timestamp

data class ProductItem(
    var fileId: String? = null,
    var title: String? = null,
    var description: String? = null,
    var category: String? = null,
    var primaryImage: String? = null,
    var otherImages: MutableList<String>? = null,
    var timeStamp: Timestamp? = null,
    var price: Double? = 0.00,
    var discountPercentage: Double? = 0.00,
    var rating: Double? = 0.00,
    var stock: Int? = 0,
    var tags: List<String>? = null,
    var brand: String? = null,
    var sku: String? = null,
    var weight: Int? = 0,
    var dimensions: Dimensions? = null,
    var warrantyInformation: String? = null,
    var shippingInformation: String? = null,
    var availabilityStatus: String? = null,
    var reviews: List<Review>? = null,
    var returnPolicy: String? = null,
    var minimumOrderQuantity: Int? = 0,
    var meta: Meta? = null,
)

data class Dimensions(
    var width: Double? = 0.00,
    var height: Double? = 0.00,
    var depth: Double? = 0.00
)

data class Review(
    var rating: Int? = 0,
    var comment: String? = null,
    var date: String? = null,
    var reviewerName: String? = null,
    var reviewerEmail: String? = null
)

data class Meta(
    var createdBy: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var barcode: String? = null,
    var qrCode: String? = null
)
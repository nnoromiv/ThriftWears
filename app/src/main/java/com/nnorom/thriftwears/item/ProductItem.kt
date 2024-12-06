package com.nnorom.thriftwears.item

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
    var discountPercentage: Double? = 4.00,
    var rating: Double? = 3.40,
    var stock: Int? = 43,
    var tags: List<String>? = null,
    var brand: String? = "Thrift Wears",
    var sku: String? = null,
    var weight: Int? = 10,
    var dimensions: Dimensions? = Dimensions(),
    var warrantyInformation: String? = "1 year warranty",
    var shippingInformation: String? = "Standard shipping",
    var availabilityStatus: String? = "In Stock",
    var reviews: List<Review>? = null,
    var returnPolicy: String? = "30 days return policy",
    var minimumOrderQuantity: Int? = 14,
    var meta: Meta? = Meta(),
)

data class Dimensions(
    var width: Double? = 10.00,
    var height: Double? = 10.00,
    var depth: Double? = 10.00
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
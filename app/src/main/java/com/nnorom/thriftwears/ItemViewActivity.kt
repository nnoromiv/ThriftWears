package com.nnorom.thriftwears

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.ULocale
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nnorom.thriftwears.adapter.MoreImagesAdapter
import com.nnorom.thriftwears.databinding.ActivityItemViewBinding
import com.nnorom.thriftwears.item.CartItem
import com.nnorom.thriftwears.item.MoreImagesItem
import com.nnorom.thriftwears.item.ProductItem
import com.nnorom.thriftwears.item.UserItem
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import java.util.Date
import kotlin.collections.ArrayList

class ItemViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemViewBinding
    private var currentState: String? = null
    private val db = Firebase.firestore
    private var isSaved = false
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val MODEL_KEY = "ITEM_MODEL_KEY"
        private const val TAG = "ITEM_VIEW"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Inflate layout with ViewBinding
        binding = ActivityItemViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        window.statusBarColor = getColor(R.color.primary)

        // Restore previous state or set initial UI state
        currentState = savedInstanceState?.getString(MODEL_KEY)
        if (currentState == null) {
            initializeUI()
        }

        val bundle: Bundle? = intent.extras
        val fileId = bundle!!.getString("fileId")

        checkIfFileIdIsSaved(fileId!!){ exists ->
            if (exists) {
                isSaved = true
                binding.savedButton.setImageResource(R.drawable.saved_solid)
            } else {
                isSaved = false
                binding.savedButton.setImageResource(R.drawable.saved)
            }
        }

        db.collection("products")
            .whereEqualTo("fileId", fileId)
            .limit(1) // Limit the query to return only one result
            .get()
            .addOnSuccessListener { results ->
                val product = results.documents.firstOrNull()?.toObject(ProductItem::class.java)
                if (product != null) {
                    Log.d("ITEM_VIEW", "Product: $product")
                    binding.name.text = product.title

                    Picasso.get()
                        .load(product.primaryImage)
                        .fit()
                        .centerCrop()
                        .tag("IMAGE_LOADING")
                        .into(binding.image)

                    binding.moreImagesRecyclerView.layoutManager = LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                    val moreImagesList = product.otherImages?.mapIndexed { index, url ->
                        MoreImagesItem(id = "image${index + 1}", image = Uri.parse(url))
                    } ?: emptyList()

                    binding.moreImagesRecyclerView.adapter = MoreImagesAdapter(moreImagesList)
                    binding.price.text= buildString {
                        append('£')
                        append(product.price)
                    }

                    binding.itemBuyButton.setOnClickListener {
                        val item = if (product.fileId != null && product.title != null && product.price != null) {
                            CartItem(
                                product.fileId!!,
                                product.title!!,
                                product.price!!,
                                1
                            )
                        } else null

                        if (item != null) {
                            val intent = Intent(this, CartActivity::class.java)
                            val cartData = mutableListOf(item)
                            intent.putParcelableArrayListExtra("cart_data", ArrayList(cartData))
                            this.startActivity(intent)
                            showToast("Added to cart")
                        }
                    }

                    binding.description.text = product.description
                    product.shippingInformation.also { binding.delivery.text = it }

                    db.collection("users")
                        .whereEqualTo("id", product.meta!!.createdBy)
                        .limit(1) // Limit the query to return only one result
                        .get()
                        .addOnSuccessListener { item ->
                            val user = item.documents.firstOrNull()?.toObject(UserItem::class.java)
                            if (user != null) {
                                Log.d("ITEM_VIEW", "User: $user")
                                val fullName = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim()
                                fullName.also { binding.sellerName.text = it }

                                val memberSince = user.timeStamp?.let { memberDate(it) }
                                memberSince.also { binding.sellerYear.text = it }

                                binding.contactSellerButton.setOnClickListener {
                                    val phoneNumber = user.phoneNumber // Make sure this includes the country code (e.g., +1234567890)
                                    val whatsappIntent = Intent(Intent.ACTION_VIEW)

                                    try {
                                        whatsappIntent.data = Uri.parse("https://wa.me/$phoneNumber")
                                        startActivity(whatsappIntent)
                                    } catch (e: Exception) {
                                        showToast("WhatsApp is not installed on this device")
                                    }
                                }
                            } else {
                                Log.d("ITEM_VIEW", "No seller found for product found with fileId: $fileId")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("HOME_BODY", "Error getting seller: ", exception)
                        }

                } else {
                    Log.d("ITEM_VIEW", "No product found with fileId: $fileId")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ITEM_VIEW", "Error getting document: ", exception)
            }

        binding.savedButton.setOnClickListener {
            if (isSaved) {
                // Currently saved, so remove from wishlist
                binding.savedButton.setImageResource(R.drawable.saved)
                removeFromSavedList(fileId)
                showToast("Removed from Wishlist")
            } else {
                // Not saved, so save to wishlist
                binding.savedButton.setImageResource(R.drawable.saved_solid)
                addToSavedList(fileId)
                showToast("Saved to Wishlist")
            }

            // Toggle the state
            isSaved = !isSaved
        }

    }

    private fun initializeUI() {
        // Set up UI elements
        binding.itemBuyButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
        binding.contactSellerButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentState)
    }

    @SuppressLint("SimpleDateFormat")
    private fun memberDate(timestamp: Timestamp): String {
        val date = Date(timestamp.seconds * 1000)
        val outputFormat = SimpleDateFormat("MMM. yyyy", ULocale.ENGLISH)

        return "Member since" + " " + outputFormat.format(date)
    }

    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun addToSavedList(fileId: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w(TAG, "User is not authenticated")
            showToast("User is not authenticated")
            return
        }

        val timeStamp = Timestamp.now()
        val data = hashMapOf(
            "fileId" to fileId,
            "timeStamp" to timeStamp
        )

        checkIfFileIdIsSaved(fileId) { exists ->
            if(!exists) {
                db.collection("saved")
                    .document(uid)
                    .collection("products")
                    .add(data)
                    .addOnSuccessListener { docRef ->
                        Log.d(TAG, "File saved successfully with ID: ${docRef.id}")
                        showToast("File saved successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        showToast("Failed to save file data")
                    }
            }
        }
    }

    private fun removeFromSavedList(fileId: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w(TAG, "User is not authenticated")
            showToast("User is not authenticated")
            return
        }

        db.collection("saved")
            .document(uid)
            .collection("products")
            .whereEqualTo("fileId", fileId) // Find the document with the matching fileId
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        db.collection("saved")
                            .document(uid)
                            .collection("products")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "File removed successfully: ${document.id}")
                                showToast("File removed successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error deleting document", e)
                                showToast("Failed to remove file data")
                            }
                    }
                } else {
                    Log.w(TAG, "No matching file found")
                    showToast("No matching file found")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching document", e)
                showToast("Failed to fetch file data")
            }
    }

    private fun checkIfFileIdIsSaved(fileId: String, callback: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w(TAG, "User is not authenticated")
            showToast("User is not authenticated")
            callback(false)
            return
        }

        db.collection("saved")
            .document(uid)
            .collection("products")
            .whereEqualTo("fileId", fileId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val isSaved = !querySnapshot.isEmpty
                callback(isSaved) // Notify caller of the result
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching document", e)
                showToast("Failed to fetch file data")
                callback(false) // Notify caller of the failure
            }
    }

}

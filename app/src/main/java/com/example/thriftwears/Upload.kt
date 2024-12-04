package com.example.thriftwears

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.thriftwears.databinding.UploadBinding
import com.example.thriftwears.item.ProductItem
import com.example.thriftwears.item.UploadedImagesItem
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class Upload : Fragment() {

    private var _binding: UploadBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val storage = Firebase.storage("gs://thriftwears-78671.firebasestorage.app")
    private val db = com.google.firebase.Firebase.firestore

    private val categories = listOf("Men", "Women")

    private var moreImagesList = mutableListOf(
        UploadedImagesItem(null),
    )

    companion object{
        const val IMAGE_REQUEST_CODE = 100
        private const val TAG = "UPLOAD"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth // Initialize FirebaseAuth instance
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            updateUI()
        } else {
            Log.d(TAG, "User is signed in: $currentUser.uid")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UploadBinding.inflate(inflater, container, false)

        binding.uploadButton.backgroundTintList = ContextCompat.getColorStateList(this.requireContext(), R.color.primary)

        val imageViews = listOf(binding.image2, binding.image3, binding.image4, binding.image5)

        if (moreImagesList.isNotEmpty()) {
            val currentItem = moreImagesList[0]

            // Load the primary image or hide the view if it's null
            if (currentItem.image1 != null) {
                Picasso.get()
                    .load(currentItem.image1.toString())
                    .fit()
                    .centerCrop()
                    .tag("IMAGE_LOADING")
                    .into(binding.primaryImage)
                binding.primaryImage.visibility = View.VISIBLE
            } else {
                binding.primaryImage.visibility = View.GONE
            }

            // List of image URIs and corresponding views
            val imageUris = listOf(currentItem.image2, currentItem.image3, currentItem.image4, currentItem.image5)

            // Iterate through image URIs and views, loading or hiding as needed
            imageUris.forEachIndexed { index, uri ->
                if (uri != null) {
                    Picasso.get()
                        .load(uri.toString())
                        .fit()
                        .centerCrop()
                        .tag("IMAGE_LOADING")
                        .into(imageViews[index])
                    imageViews[index].visibility = View.VISIBLE

                    imageViews[index].setOnClickListener {
                        Log.d(TAG, "Image $index clicked")
                        swapImages(index + 1, currentItem)
                    }
                } else {
                    imageViews[index].visibility = View.GONE
                }
            }
        } else {
            // Hide all views if the list is empty
            binding.primaryImage.visibility = View.GONE
            binding.image2.visibility = View.GONE
            binding.image3.visibility = View.GONE
            binding.image4.visibility = View.GONE
            binding.image5.visibility = View.GONE

            Log.e(TAG, "No images to display in moreImagesList.")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dropdownMenu: AutoCompleteTextView = binding.upload.findViewById(R.id.dropdownMenu)
        val adapter = ArrayAdapter(this.requireContext(), R.layout.dropdown_item, categories)
        dropdownMenu.setAdapter(adapter)

        if (moreImagesList.isNotEmpty()) {
            val currentItem = moreImagesList[0]

            val imageViews = listOf(binding.image2, binding.image3, binding.image4, binding.image5)
            val imageUris = listOf(currentItem.image2, currentItem.image3, currentItem.image4, currentItem.image5)

            imageUris.forEachIndexed { index, _ ->
                imageViews[index].setOnClickListener {
                    Log.d(TAG, "Image $index clicked")
                    swapImages(index + 1, currentItem)
                }
            }
        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }

        binding.uploadImageByGallery.setOnClickListener{
            pickFromGallery()
        }

        binding.uploadButton.setOnClickListener {
            val uploadTasks = mutableListOf<Task<Uri>>()
            val title = binding.uploadTitle.text.toString()
            val description = binding.uploadDescription.text.toString()
            val category = binding.dropdownMenu.text.toString().lowercase()
            val price = binding.uploadPrice.text.toString()
            val fileId = generateRandomId()

            if (!validateInput(title, description, category, price)) return@setOnClickListener
            if(moreImagesList.isEmpty()) return@setOnClickListener

            val productItemData = ProductItem(
                fileId,
                title,
                description,
                category,
                null,
                null,
                null,
                price.toDouble(),
            )

            val currentItem = moreImagesList[0]
            val imageUris = listOf(currentItem.image1, currentItem.image2, currentItem.image3, currentItem.image4, currentItem.image5)
            imageUris.forEachIndexed {index, uri ->
                if(uri != null) {
                    val uploadTask = uploadImage(fileId, index, uri, productItemData)
                    uploadTasks.add(uploadTask)
                }
            }

            Tasks.whenAllComplete(uploadTasks)
                .addOnSuccessListener {
                    saveItemToFireStore(productItemData)
                }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Picasso.get().cancelTag("IMAGE_LOADING")
        _binding = null // Avoid memory leaks
    }

    private fun updateUI() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showToast(message: String) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    private fun swapImages(index: Int, currentItem: UploadedImagesItem) {
        val primaryUri = currentItem.image1
        val secondaryUri = when (index) {
            1 -> currentItem.image2
            2 -> currentItem.image3
            3 -> currentItem.image4
            4 -> currentItem.image5
            else -> null
        }

        if (secondaryUri != null) {
            // Update primary image
            currentItem.image1 = secondaryUri
            Picasso.get()
                .load(secondaryUri.toString())
                .fit()
                .centerCrop()
                .tag("IMAGE_LOADING")
                .into(binding.primaryImage)

            // Update secondary image
            when (index) {
                1 -> currentItem.image2 = primaryUri
                2 -> currentItem.image3 = primaryUri
                3 -> currentItem.image4 = primaryUri
                4 -> currentItem.image5 = primaryUri
            }

            val secondaryImageView = when (index) {
                1 -> binding.image2
                2 -> binding.image3
                3 -> binding.image4
                4 -> binding.image5
                else -> null
            }

            if (primaryUri != null && secondaryImageView != null) {
                Picasso.get()
                    .load(primaryUri.toString())
                    .fit()
                    .centerCrop()
                    .tag("IMAGE_LOADING")
                    .into(secondaryImageView)
            }
        }
    }

    private fun pickFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                // List of image views
                val imageViews = listOf(binding.primaryImage, binding.image2, binding.image3, binding.image4, binding.image5)

                // List of image slots in UploadedImagesItemClass
                val imageUris = listOf(
                    moreImagesList[0].image1,
                    moreImagesList[0].image2,
                    moreImagesList[0].image3,
                    moreImagesList[0].image4,
                    moreImagesList[0].image5
                )

                // Find the first available (null) slot
                val emptySlotIndex = imageUris.indexOfFirst { it == null }

                if (emptySlotIndex != -1) {
                    // Update the corresponding ImageView and UploadedImagesItemClass property
                    imageViews[emptySlotIndex].setImageURI(imageUri)
                    imageViews[emptySlotIndex].visibility = View.VISIBLE

                    when (emptySlotIndex) {
                        0 -> moreImagesList[0].image1 = imageUri
                        1 -> moreImagesList[0].image2 = imageUri
                        2 -> moreImagesList[0].image3 = imageUri
                        3 -> moreImagesList[0].image4 = imageUri
                        4 -> moreImagesList[0].image5 = imageUri
                    }

                    // Display a toast indicating which image slot was updated
                    showToast("Image set in slot ${emptySlotIndex + 1}")
                } else {
                    showToast("No available slots to add the image")
                }
            }
        }
    }

    private fun validateInput(title: String, description: String, category: String, price: String): Boolean {
        if (title.isEmpty() || description.isEmpty() || category.isEmpty() || price.isEmpty()) {
            showToast("All fields are required")
            return false
        }

        if(!categories.contains(category)){
            showToast("Invalid category")
            return false
        }

        if (moreImagesList.any { it.image1 == null }) {
            showToast("Please select at least one image")
            return false
        }

        return true
    }

    private fun uploadImage(fileId: String, fileName:Int, filePath:Uri, productItemData: ProductItem = ProductItem()): Task<Uri> {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/${auth.currentUser!!.uid}/$fileId/${fileName}.jpg")

        val inputStream = try {
            requireContext().contentResolver.openInputStream(filePath)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening file stream", e)
            null
        }

        return inputStream?.let {
            imagesRef.putStream(it)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception ?: Exception("Unknown upload error")
                    }
                    imagesRef.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    showToast("Upload Success")
                    when (fileName) {
                        0 -> {
                            productItemData.primaryImage = uri.toString()
                        }
                        1 -> {
                            productItemData.otherImages = mutableListOf(uri.toString())
                        }
                        else -> {
                            productItemData.otherImages?.add(uri.toString())
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error uploading image", e)
                    showToast("Upload Failed")
                }
        } ?: Tasks.forException(Exception("Input stream is null"))
    }

    private fun generateRandomId(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun saveItemToFireStore(productItem: ProductItem) {
        productItem.meta!!.createdBy = auth.currentUser!!.uid
        productItem.timeStamp = com.google.firebase.Timestamp.now()
        productItem.meta!!.createdAt = com.google.firebase.Timestamp.now().toString()
        productItem.sku = generateRandomId()

        productItem.fileId?.let {
            db.collection("products")
                .document(it)
                .set(productItem)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "documentUpload:success, user: $docRef")
                    updateUI()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    showToast("Failed to save user data")
                }
        }
    }

}
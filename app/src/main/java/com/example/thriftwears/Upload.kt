package com.example.thriftwears

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.thriftwears.databinding.UploadBinding
import com.example.thriftwears.item.UploadedImagesItemClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class Upload : Fragment() {

    private var _binding: UploadBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var imageVieww: ImageView

    private val categories = listOf("Option 1", "Option 2", "Option 3")

    private val img = Uri.parse("https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D")
    private val img1 = Uri.parse("https://images.unsplash.com/uploads/141104078198192352262/c9aa631b?q=80&w=1746&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
    private val img2 = Uri.parse("https://images.unsplash.com/photo-1500531359996-c89a0e63e49c?q=80&w=1738&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
    private val img3 = Uri.parse("https://plus.unsplash.com/premium_photo-1693243521806-ec8ddb5c4581?q=80&w=1587&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
    private val img4 = Uri.parse("https://plus.unsplash.com/premium_photo-1722859221349-26353eae4744?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8ZG9nfGVufDB8fDB8fHww")

    private var moreImagesList = mutableListOf(
        UploadedImagesItemClass(
            null,
            null,
            null,
            null,
            null
        ),
    )

    companion object{
        val IMAGE_REQUEST_CODE = 100
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
            Log.d("UPLOAD", "User is signed in: $currentUser.uid")
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
            val imageViews = listOf(binding.image2, binding.image3, binding.image4, binding.image5)

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
                        swapImages(index + 1, currentItem) // Index + 1 because image1 is primary
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

            Log.e("UPLOAD", "No images to display in moreImagesList.")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dropdownMenu: AutoCompleteTextView = binding.upload.findViewById(R.id.dropdownMenu)
        val adapter = ArrayAdapter(this.requireContext(), R.layout.dropdown_item, categories)
        dropdownMenu.setAdapter(adapter)

        binding.closeButton.setOnClickListener {
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }

        binding.uploadImageByGallery.setOnClickListener{
            pickFromGallery()
        }

        binding.uploadButton.setOnClickListener {
            val title = binding.uploadTitle.text.toString()
            val description = binding.uploadDescription.text.toString()
            val category = binding.dropdownMenu.text.toString()

            if (!validateInput(title, description, category)) return@setOnClickListener
            Log.d("UPLOAD", "Images: $moreImagesList")
            Log.d("UPLOAD", "Title: $title, Description: $description, Category: $category")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Picasso.get().cancelTag("IMAGE_LOADING")
        _binding = null // Avoid memory leaks
    }

    private fun updateUI() {
        // Navigate to LoginActivity and finish the parent activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showToast(message: String) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    private fun swapImages(index: Int, currentItem: UploadedImagesItemClass) {
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
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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

    private fun validateInput(title: String, description: String, category: String): Boolean {
        if (title.isEmpty() || description.isEmpty() || category.isEmpty()) {
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
}
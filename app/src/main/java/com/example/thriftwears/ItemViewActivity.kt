package com.example.thriftwears

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thriftwears.adapter.MoreImagesAdapter
import com.example.thriftwears.adapter.SavedCardViewAdapter
import com.example.thriftwears.databinding.ActivityItemViewBinding
import com.example.thriftwears.item.MoreImagesItemClass
import com.squareup.picasso.Picasso

class ItemViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemViewBinding
    private var currentState: String? = null

    private val img = Uri.parse("https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D")

    private val moreImagesList = listOf(
        MoreImagesItemClass(  "image001",img),
        MoreImagesItemClass(  "image002",img),
        MoreImagesItemClass(  "image003",img),
        MoreImagesItemClass(  "image004",img),
    )

    companion object {
        private const val MODEL_KEY = "ITEM_MODEL_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Inflate layout with ViewBinding
        binding = ActivityItemViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore previous state or set initial UI state
        currentState = savedInstanceState?.getString(MODEL_KEY)
        if (currentState == null) {
            initializeUI()
        }

        val bundle: Bundle? = intent.extras
        val iName = bundle!!.getString("name")
        val iImage = bundle!!.getString("image")
        val iPrice = bundle!!.getString("price")
        val iDescription = bundle!!.getString("description")

       binding.name.text = iName
        Picasso.get()
            .load(iImage)
            .fit()
            .centerCrop()
            .tag("IMAGE_LOADING")
            .into(binding.image)

        binding.moreImagesRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        binding.moreImagesRecyclerView.adapter = MoreImagesAdapter(moreImagesList)
        binding.price.text= buildString {
            append('£')
            append(iPrice)
        }

        binding.description.text = iDescription
        binding.delivery.text = "Free delivery for 2 to 3 days"
        binding.sellerName.text = "John Doe"

        binding.sellerYear.text= buildString {
            append("Member since")
            append("2024")
        }

    }

    private fun initializeUI() {
        // Set up UI elements
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentState)
    }
}

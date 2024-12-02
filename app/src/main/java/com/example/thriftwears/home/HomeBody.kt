package com.example.thriftwears.home

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.R
import com.example.thriftwears.adapter.CardViewAdapter
import com.example.thriftwears.components.Title
import com.example.thriftwears.item.ProductItem
import com.example.thriftwears.viewmodel.GlobalCartViewModel
import com.google.firebase.firestore.firestore

class HomeBody @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val globalCartViewModel: GlobalCartViewModel
) : LinearLayout(context, attrs, defStyleAttr) {

    private val db = com.google.firebase.Firebase.firestore
    private val img = "https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D"

    private val cardViewList = listOf(
        ProductItem(
            "item001",
            "Sweater",
            "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Near for glorious of fabled knew. Name him high passed little might known of a,.",
            "local",
            img,
            null,
            null,
            200.0
        ),
        ProductItem(
            "item001",
            "Sweater",
            "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Near for glorious of fabled knew. Name him high passed little might known of a,.",
            "local",
            img,
            null,
            null,
            200.0
        ),
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.home_body, this, true)

        "Trending Now".also { findViewById<Title>(R.id.googleTitle).findViewById<TextView>(R.id.titleText).text = it }

        val googleRecyclerView = findViewById<RecyclerView>(R.id.googleRecyclerView)
        val amazonRecyclerView = findViewById<RecyclerView>(R.id.amazonRecyclerView)

        googleRecyclerView.layoutManager = GridLayoutManager(this.context, 2)
        googleRecyclerView.setHasFixedSize(true)

        db.collection("products")
            .get()
            .addOnSuccessListener { results ->
                val productList = mutableListOf<ProductItem>()
                for (document in results) {
                    val product = document.toObject(ProductItem::class.java)
                    productList.add(product)
                }
                googleRecyclerView.adapter = CardViewAdapter(productList, globalCartViewModel)
            }
            .addOnFailureListener { exception ->
                Log.d("HOME_BODY", "Error getting documents: ", exception)
            }

        amazonRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        amazonRecyclerView.adapter = CardViewAdapter(cardViewList, globalCartViewModel)
    }
}

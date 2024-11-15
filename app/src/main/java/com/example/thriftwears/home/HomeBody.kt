package com.example.thriftwears.home

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.R
import com.example.thriftwears.card.Adapter
import com.example.thriftwears.card.Item
import com.example.thriftwears.components.Title

class HomeBody @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val img = Uri.parse("https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D")

    private val cardViewList = listOf(
        Item("Sweater", img, "Hurricane loves me", 200.0),
        Item("Sweater", img, "Hurricane loves me", 200.0),
        Item("Sweater", img, "Hurricane loves me", 200.0),
        Item("Sweater", img, "Hurricane loves me", 200.0),
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.home_body, this, true)

        "Trending in Google".also { findViewById<Title>(R.id.googleTitle).findViewById<TextView>(R.id.titleText).text = it }

        val googleRecyclerView = findViewById<RecyclerView>(R.id.googleRecyclerView)
        val amazonRecyclerView = findViewById<RecyclerView>(R.id.amazonRecyclerView)

        googleRecyclerView.layoutManager = LinearLayoutManager(
            this.context, LinearLayoutManager.HORIZONTAL, false
        )
        googleRecyclerView.adapter = Adapter(cardViewList)

        "Trending in Amazon".also { findViewById<Title>(R.id.amazonTitle).findViewById<TextView>(R.id.titleText).text = it }
        amazonRecyclerView.layoutManager = LinearLayoutManager(
            this.context, LinearLayoutManager.HORIZONTAL, false
        )
        amazonRecyclerView.adapter = Adapter(cardViewList)
    }

}

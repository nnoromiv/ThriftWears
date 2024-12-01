package com.example.thriftwears.home

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.R
import com.example.thriftwears.adapter.CardViewAdapter
import com.example.thriftwears.components.Title
import com.example.thriftwears.item.CardViewItem

class HomeBody @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val img = Uri.parse("https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D")

    private val cardViewList = listOf(
        CardViewItem(  "item001","Sweater", img, "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Near for glorious of fabled knew. Name him high passed little might known of a,.", 200.0),
        CardViewItem(  "item002", "Shirts", img, "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Near for glorious of fabled knew. Name him high passed little might known of a,.", 400.0),
        CardViewItem(  "item003", "Joggers", img, "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Near for glorious of fabled knew. Name him high passed little might known of a,.", 600.0),
        CardViewItem(  "item004", "Nike air", img, "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Near for glorious of fabled knew. Name him high passed little might known of a,.", 800.0),
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.home_body, this, true)

        "Trending in Google".also { findViewById<Title>(R.id.googleTitle).findViewById<TextView>(R.id.titleText).text = it }

        val googleRecyclerView = findViewById<RecyclerView>(R.id.googleRecyclerView)
        val amazonRecyclerView = findViewById<RecyclerView>(R.id.amazonRecyclerView)

        googleRecyclerView.layoutManager = LinearLayoutManager(
            this.context, LinearLayoutManager.HORIZONTAL, false
        )
        googleRecyclerView.adapter = CardViewAdapter(cardViewList)

        "Trending in Amazon".also { findViewById<Title>(R.id.amazonTitle).findViewById<TextView>(R.id.titleText).text = it }
        amazonRecyclerView.layoutManager = LinearLayoutManager(
            this.context, LinearLayoutManager.HORIZONTAL, false
        )
        amazonRecyclerView.adapter = CardViewAdapter(cardViewList)
    }

}

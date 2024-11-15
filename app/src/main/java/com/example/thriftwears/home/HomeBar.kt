package com.example.thriftwears.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.example.thriftwears.R

class HomeBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.home_bar, this, true)

        findViewById<ImageButton>(R.id.cartButton).setOnClickListener{
            val message = "Cart Clicked"
            Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
        }
    }

}

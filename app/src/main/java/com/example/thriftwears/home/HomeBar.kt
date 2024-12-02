package com.example.thriftwears.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.example.thriftwears.CartActivity
import com.example.thriftwears.R
import com.example.thriftwears.viewmodel.GlobalCartViewModel
import java.util.ArrayList

class HomeBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val globalCartViewModel: GlobalCartViewModel
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.home_bar, this, true)

        findViewById<ImageButton>(R.id.cartButton).setOnClickListener{
            val intent = Intent(context, CartActivity::class.java)
            val cartData = globalCartViewModel.items.value
            intent.putParcelableArrayListExtra("cart_data", ArrayList(cartData))
            context.startActivity(intent)
        }
    }

}

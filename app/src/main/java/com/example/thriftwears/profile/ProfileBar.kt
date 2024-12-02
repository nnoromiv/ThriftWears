package com.example.thriftwears.profile

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import com.example.thriftwears.CartActivity
import com.example.thriftwears.R
import com.example.thriftwears.viewmodel.GlobalCartViewModel
import java.util.ArrayList

class ProfileBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val globalCartViewModel: GlobalCartViewModel
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.profile_bar, this, true)

        findViewById<ImageButton>(R.id.cartButton).setOnClickListener{
            val intent = Intent(context, CartActivity::class.java)
            val cartData = globalCartViewModel.items.value
            intent.putParcelableArrayListExtra("cart_data", cartData?.let { it1 -> ArrayList(it1) })
            context.startActivity(intent)
        }
    }

}

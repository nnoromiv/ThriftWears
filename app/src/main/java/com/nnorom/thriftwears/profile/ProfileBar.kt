package com.nnorom.thriftwears.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import com.nnorom.thriftwears.CartActivity
import com.nnorom.thriftwears.R
import com.nnorom.thriftwears.viewmodel.GlobalCartViewModel

@SuppressLint("ViewConstructor")
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

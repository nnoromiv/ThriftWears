package com.example.thriftwears.profile

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.thriftwears.MainActivity
import com.example.thriftwears.R
import com.example.thriftwears.components.Title

class ProfileBody @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.profile_body, this, true)

        "Shopping".also { findViewById<Title>(R.id.shoppingTitle).findViewById<TextView>(R.id.titleText).text = it }
        findViewById<LinearLayout>(R.id.shoppingHome).setOnClickListener{
            val intent = Intent(this.context, MainActivity::class.java)
            this.context.startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.shoppingSaved).setOnClickListener{
//            val intent = Intent(this.context, MainActivity::class.java)
//            this.context.startActivity(intent)
        }

        "Shortcuts".also { findViewById<Title>(R.id.shortCutTitle).findViewById<TextView>(R.id.titleText).text = it }
        "Account".also { findViewById<Title>(R.id.accountTitle).findViewById<TextView>(R.id.titleText).text = it }
    }

}

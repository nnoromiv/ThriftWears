package com.nnorom.thriftwears.profile

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.nnorom.thriftwears.LoginActivity
import com.nnorom.thriftwears.MainActivity
import com.nnorom.thriftwears.R
import com.nnorom.thriftwears.components.Title
import com.google.firebase.auth.FirebaseAuth

class ProfileBody @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    init {
        LayoutInflater.from(context).inflate(R.layout.profile_body, this, true)

        "Shopping".also { findViewById<Title>(R.id.shoppingTitle).findViewById<TextView>(R.id.titleText).text = it }
        findViewById<LinearLayout>(R.id.shoppingHome).setOnClickListener{
            val intent = Intent(this.context, MainActivity::class.java)
            this.context.startActivity(intent)
        }

        findViewById<TextView>(R.id.paymentMethods).setOnClickListener{
            Toast.makeText(this.context, "Paypal Only", Toast.LENGTH_SHORT).show()
        }


        "Account".also { findViewById<Title>(R.id.accountTitle).findViewById<TextView>(R.id.titleText).text = it }
        findViewById<TextView>(R.id.accountLogOut).setOnClickListener{
            auth.signOut()

            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

}

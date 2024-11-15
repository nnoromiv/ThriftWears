package com.example.thriftwears.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.example.thriftwears.R
import com.google.android.material.textfield.TextInputEditText

class SearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.search_bar, this, true)

        findViewById<TextInputEditText>(R.id.homeSearchInput).setOnKeyListener {
            _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                Toast.makeText(this.context, "Search clicked", Toast.LENGTH_SHORT).show()
                true
            } else {
                false
            }
        }
    }


}

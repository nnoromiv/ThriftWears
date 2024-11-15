package com.example.thriftwears.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.thriftwears.R

class SavedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.saved_card_view, this, true)
    }

}

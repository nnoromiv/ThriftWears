package com.example.thriftwears.home

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.example.thriftwears.R
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase


class Chips @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var auth: FirebaseAuth
    private val db = com.google.firebase.Firebase.firestore

    init {
        LayoutInflater.from(context).inflate(R.layout.chips, this, true)
        auth = Firebase.auth // Initialize FirebaseAuth instance

        val savedChip = findViewById<Chip>(R.id.savedChip)
        val localChip = findViewById<Chip>(R.id.localChip)
        val festiveChip = findViewById<Chip>(R.id.festiveChip)
        val fashionChip = findViewById<Chip>(R.id.fashionChip)

        // Set each chip as checkable
        savedChip.isCheckable = true
        localChip.isCheckable = true
        festiveChip.isCheckable = true
        fashionChip.isCheckable = true

        // Function to handle the chip toggle and update appearance
        fun toggleChipAppearance(chip: Chip) {
            val isChecked = chip.isChecked
            Toast.makeText(context, "${chip.text} Chip: $isChecked", Toast.LENGTH_SHORT).show()
            Log.i("Chip", "${chip.text} chip checked changed: $isChecked")

            if (isChecked) {
                chip.setTextColor(resources.getColor(R.color.white, context.theme))
                chip.chipBackgroundColor = resources.getColorStateList(R.color.primary, context.theme)
            } else {
                chip.setTextColor(resources.getColor(R.color.black, context.theme))
                chip.chipBackgroundColor = resources.getColorStateList(R.color.lightGrey, context.theme)
            }
        }

        // Set onClickListeners for each chip
        savedChip.setOnClickListener { toggleChipAppearance(savedChip) }
        localChip.setOnClickListener { toggleChipAppearance(localChip) }
        festiveChip.setOnClickListener { toggleChipAppearance(festiveChip) }
        fashionChip.setOnClickListener { toggleChipAppearance(fashionChip) }
    }

}

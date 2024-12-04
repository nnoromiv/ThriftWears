package com.example.thriftwears.home

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.R
import com.example.thriftwears.adapter.CardViewAdapter
import com.example.thriftwears.components.Title
import com.example.thriftwears.item.ProductItem
import com.example.thriftwears.viewmodel.GlobalCartViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.firestore

class HomeBody @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val globalCartViewModel: GlobalCartViewModel,
    chips: Chips,
    ) : LinearLayout(context, attrs, defStyleAttr) {

    private val db = com.google.firebase.Firebase.firestore
    private val img = "https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D"

    private val cardViewList = listOf(
        ProductItem(
            "item001",
            "Sweater",
            "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Near for glorious of fabled knew. Name him high passed little might known of a,.",
            "local",
            img,
            null,
            null,
            200.0
        ),
        ProductItem(
            "item001",
            "Sweater",
            "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Near for glorious of fabled knew. Name him high passed little might known of a,.",
            "local",
            img,
            null,
            null,
            200.0
        ),
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.home_body, this, true)

        val chipGroup = chips.findViewById<ChipGroup>(R.id.chipGroup)
        val savedChip = chips.findViewById<Chip>(R.id.savedChip)
        val menChip = chips.findViewById<Chip>(R.id.menChip)
        val womenChip = chips.findViewById<Chip>(R.id.womenChip)
        val googleRecyclerView: RecyclerView = findViewById(R.id.googleRecyclerView)
        val amazonRecyclerView: RecyclerView = findViewById(R.id.amazonRecyclerView)


        googleRecyclerView.layoutManager = GridLayoutManager(this.context, 2)
        googleRecyclerView.setHasFixedSize(true)

        savedChip.isCheckable = true
        menChip.isCheckable = true
        womenChip.isCheckable = true

        fun toggleChipAppearance(chip: Chip) {
            val isChecked = chip.isChecked

            // Loop through all chips in the ChipGroup and uncheck others
            for (i in 0 until chipGroup.childCount) {
                val child = chipGroup.getChildAt(i)
                if (child is Chip && child != chip) {
                    child.isChecked = false
                    child.setTextColor(resources.getColor(R.color.black, context.theme))
                    child.chipBackgroundColor = resources.getColorStateList(R.color.lightGrey, context.theme)
                }
            }

            if (isChecked) {
                chip.setTextColor(resources.getColor(R.color.white, context.theme))
                chip.chipBackgroundColor = resources.getColorStateList(R.color.primary, context.theme)
                val value = chip.text.toString().lowercase()


                db.collection("products")
                    .whereGreaterThanOrEqualTo("category", value)
                    .whereLessThan("category", value + "\uf8ff")
                    .get()
                    .addOnSuccessListener { results ->
                        val productList = mutableListOf<ProductItem>()
                        for (document in results) {
                            val product = document.toObject(ProductItem::class.java)
                            productList.add(product)
                        }
                        googleRecyclerView.adapter = CardViewAdapter(productList, globalCartViewModel)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("HOME_BODY", "Error getting documents: ", exception)
                    }
            } else {
                chip.setTextColor(resources.getColor(R.color.black, context.theme))
                chip.chipBackgroundColor = resources.getColorStateList(R.color.lightGrey, context.theme)
                populateBody()
            }

            Toast.makeText(context, "${chip.text} Chip: $isChecked", Toast.LENGTH_SHORT).show()
            Log.i("Chip", "${chip.text} chip checked changed: $isChecked")
        }


        savedChip.setOnClickListener { toggleChipAppearance(savedChip) }
        menChip.setOnClickListener { toggleChipAppearance(menChip) }
        womenChip.setOnClickListener { toggleChipAppearance(womenChip) }

        "Trending Now".also { findViewById<Title>(R.id.googleTitle).findViewById<TextView>(R.id.titleText).text = it }

        populateBody()

        amazonRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        amazonRecyclerView.adapter = CardViewAdapter(cardViewList, globalCartViewModel)
    }

    private fun populateBody(){
        val googleRecyclerView: RecyclerView = findViewById<RecyclerView>(R.id.googleRecyclerView)

        db.collection("products")
            .get()
            .addOnSuccessListener { results ->
                val productList = mutableListOf<ProductItem>()
                for (document in results) {
                    val product = document.toObject(ProductItem::class.java)
                    productList.add(product)
                }
                googleRecyclerView.adapter = CardViewAdapter(productList, globalCartViewModel)
            }
            .addOnFailureListener { exception ->
                Log.d("HOME_BODY", "Error getting documents: ", exception)
            }
    }
}

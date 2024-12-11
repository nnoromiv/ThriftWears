package com.nnorom.thriftwears.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nnorom.thriftwears.LoginActivity
import com.nnorom.thriftwears.R
import com.nnorom.thriftwears.adapter.CardViewAdapter
import com.nnorom.thriftwears.components.SearchBar
import com.nnorom.thriftwears.components.Title
import com.nnorom.thriftwears.item.ProductItem
import com.nnorom.thriftwears.viewmodel.GlobalCartViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase
import com.nnorom.thriftwears.components.EmptySaved
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ViewConstructor")
class HomeBody @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val globalCartViewModel: GlobalCartViewModel,
    chips: Chips,
    searchBar: SearchBar
    ) : LinearLayout(context, attrs, defStyleAttr) {

    private val db = com.google.firebase.Firebase.firestore
    private val img = "https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D"
    private var searchJob: Job? = null
    private lateinit var auth: FirebaseAuth


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
        val searchInput: TextInputEditText = searchBar.findViewById(R.id.searchInput)

        googleRecyclerView.layoutManager = GridLayoutManager(this.context, 2)
        googleRecyclerView.setHasFixedSize(true)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This is called as the text is being changed
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(300)

                    val searchWord = s.toString()
                    val capitalized = capitalizeEachWord(searchWord)

                    if (capitalized.isEmpty()){
                        populateBody()
                        return@launch
                    }

                    search(capitalized)
                }

            }

            override fun afterTextChanged(s: Editable?) {
                // This is called after the text has been changed
            }
        })

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

                if(value == "saved"){
                    getSavedItems(googleRecyclerView)
                    return
                }

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

            Toast.makeText(context, "Filtered by ${chip.text} Only", Toast.LENGTH_SHORT).show()
        }

        savedChip.setOnClickListener { toggleChipAppearance(savedChip) }
        menChip.setOnClickListener { toggleChipAppearance(menChip) }
        womenChip.setOnClickListener { toggleChipAppearance(womenChip) }

        "Trending Now".also { findViewById<Title>(R.id.googleTitle).findViewById<TextView>(R.id.titleText).text = it }

        populateBody()

        amazonRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        amazonRecyclerView.adapter = CardViewAdapter(cardViewList, globalCartViewModel)
    }

    private fun showToast(message: String) = Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()

    private fun populateBody(){
        val googleRecyclerView = findViewById<RecyclerView>(R.id.googleRecyclerView)

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

    private fun search(searchWord: String){
        val googleRecyclerView = findViewById<RecyclerView>(R.id.googleRecyclerView)

        db.collection("products")
            .whereGreaterThanOrEqualTo("title", searchWord)
            .whereLessThan("title", searchWord + "\uf8ff")
            .get()
            .addOnSuccessListener { results ->
                if(results.isEmpty){
                    showToast("No results found")
                    populateBody()
                } else {
                    val productList = mutableListOf<ProductItem>()
                    for (document in results) {
                        val product = document.toObject(ProductItem::class.java)
                        productList.add(product)
                    }
                    googleRecyclerView.adapter = CardViewAdapter(productList, globalCartViewModel)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("HOME_BODY", "Error getting documents: ", exception)
            }
    }

    fun capitalizeEachWord(input: String): String {
        return input.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    private fun getSavedItems(googleRecyclerView: RecyclerView){
        auth = Firebase.auth // Initialize FirebaseAuth instance

        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w("HOME_BODY", "User is not authenticated")
            updateUI()
            return
        }

        db
            .collection("saved")
            .document(uid)
            .collection("products")
            .get()
            .addOnSuccessListener { results ->
                val savedFileIds = mutableListOf<String>()
                val productList = mutableListOf<ProductItem>()

                for (document in results) {
                    val product = document.toObject(ProductItem::class.java)
                    savedFileIds.add(product.fileId!!)
                }

                updateOnSavedList(savedFileIds, productList, googleRecyclerView)

            }
            .addOnFailureListener { exception ->
                Log.d("SAVED", "Error getting documents: ", exception)
            }
    }

    private fun updateOnSavedList(savedFileIds: MutableList<String>, productList: MutableList<ProductItem>, googleRecyclerView: RecyclerView){
        savedFileIds.forEach{ fileId ->
            db.collection("products")
                .whereEqualTo("fileId", fileId)
                .get()
                .addOnSuccessListener { results ->
                    if(results.isEmpty){
                        showToast("No saved items found")
                        populateBody()
                        return@addOnSuccessListener
                    }

                    for (document in results) {
                        val product = document.toObject(ProductItem::class.java)
                        productList.add(product)
                    }

                    googleRecyclerView.adapter = CardViewAdapter(productList, globalCartViewModel)
                }
                .addOnFailureListener{ exception ->
                    Log.d("SAVED", "Error getting documents: ", exception)
                }
        }
    }

    private fun updateUI() {
        // Navigate to LoginActivity and finish the parent activity
        val intent = Intent(this.context, LoginActivity::class.java)
        this.context.startActivity(intent)
    }
}

package com.nnorom.thriftwears

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nnorom.thriftwears.adapter.CardViewAdapter
import com.nnorom.thriftwears.databinding.SearchBinding
import com.nnorom.thriftwears.item.ProductItem
import com.nnorom.thriftwears.viewmodel.GlobalCartViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class Search(
    private val globalCartViewModel: GlobalCartViewModel
) : Fragment() {

    private var _binding: SearchBinding? = null
    private val binding get() = _binding!!
    private val db = com.google.firebase.Firebase.firestore
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleRecyclerView: RecyclerView = binding.searchRecyclerView
        val searchInput = binding.searchSearchBar.findViewById<TextInputEditText>(R.id.searchInput)

        googleRecyclerView.layoutManager = GridLayoutManager(this.context, 2)
        googleRecyclerView.setHasFixedSize(true)

        binding.emptySearchBar.visibility = View.VISIBLE

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This is called before the text is changed
                binding.emptySearchBar.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This is called as the text is being changed
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(300)

                    val searchWord = s.toString()
                    val capitalized = capitalizeEachWord(searchWord)

                    if (capitalized.isEmpty()){
                        binding.emptySearchBar.visibility = View.VISIBLE
                        return@launch
                    }

                    search(capitalized)
                }

            }

            override fun afterTextChanged(s: Editable?) {
                // This is called after the text has been changed
            }
        })

        binding.cartButton.setOnClickListener {
            val intent = Intent(requireContext(), CartActivity::class.java)
            val cartData = globalCartViewModel.items.value
            intent.putParcelableArrayListExtra("cart_data", cartData?.let { it1 -> ArrayList(it1) })
            requireContext().startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }


    private fun search(searchWord: String){
        val googleRecyclerView = binding.searchRecyclerView

        db.collection("products")
            .whereGreaterThanOrEqualTo("title", searchWord)
            .whereLessThan("title", searchWord + "\uf8ff")
            .get()
            .addOnSuccessListener { results ->
                if(results.isEmpty){
                    Toast.makeText(this.context, "No results found", Toast.LENGTH_SHORT).show()
                    binding.emptySearchBar.visibility = View.VISIBLE
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

}
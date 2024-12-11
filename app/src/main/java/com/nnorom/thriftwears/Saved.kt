package com.nnorom.thriftwears

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.nnorom.thriftwears.adapter.SavedCardViewAdapter
import com.nnorom.thriftwears.databinding.SavedBinding
import com.nnorom.thriftwears.item.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase
import com.nnorom.thriftwears.MainActivity.Companion.MAIN_ACTIVITY_KEY
import com.nnorom.thriftwears.viewmodel.GlobalCartViewModel

class Saved : Fragment(R.layout.saved) {

    private var _binding: SavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val db = com.google.firebase.Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth // Initialize FirebaseAuth instance
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("SAVED", "User is not signed in.")
            updateUI()
        } else {
            Log.d("SAVED", "User is signed in: $currentUser.uid")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.savedItemsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

        populateBody()

        binding.backButton.setOnClickListener {
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    private fun updateUI() {
        // Navigate to LoginActivity and finish the parent activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun populateBody(){
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w("HOME_BODY", "User is not authenticated")
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

                savedFileIds.forEach{ fileId ->
                    db.collection("products")
                        .whereEqualTo("fileId", fileId)
                        .get()
                        .addOnSuccessListener { results ->
                            if(results.isEmpty){
                                binding.emptySaved.visibility = View.VISIBLE
                            }
                            else{
                                binding.emptySaved.visibility = View.GONE
                                for (document in results) {
                                    val product = document.toObject(ProductItem::class.java)
                                    productList.add(product)
                                }
                                Log.d("SAVED", "File IDs: $productList")

                                binding.savedItemsRecyclerView.adapter = SavedCardViewAdapter(productList)
                            }
                        }
                        .addOnFailureListener{ exception ->
                            Log.d("SAVED", "Error getting documents: ", exception)
                        }
                }

            }
            .addOnFailureListener { exception ->
                Log.d("SAVED", "Error getting documents: ", exception)
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MAIN_ACTIVITY_KEY, Saved::class.java.simpleName)
    }

}
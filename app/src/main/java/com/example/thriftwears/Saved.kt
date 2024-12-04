package com.example.thriftwears

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.adapter.CardViewAdapter
import com.example.thriftwears.adapter.SavedCardViewAdapter
import com.example.thriftwears.databinding.SavedBinding
import com.example.thriftwears.item.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase

class Saved : Fragment() {

    private var _binding: SavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
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
                            for (document in results) {
                                val product = document.toObject(ProductItem::class.java)
                                productList.add(product)
                            }

                            binding.savedItemsRecyclerView.adapter = SavedCardViewAdapter(productList)
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

}
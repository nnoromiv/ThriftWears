package com.nnorom.thriftwears.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nnorom.thriftwears.R
import com.nnorom.thriftwears.databinding.SavedCardViewBinding
import com.nnorom.thriftwears.item.ProductItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class SavedCardViewAdapter(
    private val items: List<ProductItem>
) : RecyclerView.Adapter<SavedCardViewAdapter.SavedCardViewHolder>() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    inner class SavedCardViewHolder(private val binding: SavedCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardViewItem: ProductItem) {
            binding.name.text = cardViewItem.title
            Picasso.get()
                .load(cardViewItem.primaryImage)
                .fit()
                .centerCrop()
                .tag("IMAGE_LOADING")
                .into(binding.image)

            binding.description.text = buildString {
                append(cardViewItem.description!!.slice(0..50))
                append("...")
            }
            binding.price.text = buildString {
                append('£')
                append(cardViewItem.price.toString())
            }

            binding.savedButton.setOnClickListener {
                binding.savedButton.setImageResource(R.drawable.saved)
                removeFromSavedList(cardViewItem.fileId!!)
            }
        }

        private fun removeFromSavedList(fileId: String) {
            val uid = auth.currentUser?.uid
            if (uid == null) {
                Log.w("Saved", "User is not authenticated")
                return
            }

            db.collection("saved")
                .document(uid)
                .collection("products")
                .whereEqualTo("fileId", fileId) // Find the document with the matching fileId
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            db.collection("saved")
                                .document(uid)
                                .collection("products")
                                .document(document.id)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d("Saved", "File removed successfully: ${document.id}")
                                    showToast("File removed successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Saved", "Error deleting document", e)
                                    showToast("Failed to remove file data")
                                }
                        }
                    } else {
                        Log.w("Saved", "No matching file found")
                        showToast("No matching file found")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Saved", "Error fetching document", e)
                }
        }

        private fun showToast(message: String) = Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCardViewHolder {
        val binding = SavedCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        auth = Firebase.auth
        return SavedCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}

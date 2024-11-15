package com.example.thriftwears.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.databinding.SavedCardViewBinding
import com.squareup.picasso.Picasso

class SavedAdapter(
    private val items: List<Item>
) : RecyclerView.Adapter<SavedAdapter.SavedCardViewHolder>() {

    inner class SavedCardViewHolder(private val binding: SavedCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardViewItem: Item) {
            binding.name.text = cardViewItem.title ?: "Unknown Title"
            Picasso.get()
                .load(cardViewItem.image)
                .fit()
                .centerCrop()
                .tag("IMAGE_LOADING")
                .into(binding.image)

            binding.description.text = cardViewItem.description ?: "No description available"
            binding.price.text = buildString {
                append('£')
                append(cardViewItem.price.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCardViewHolder {
        val binding = SavedCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
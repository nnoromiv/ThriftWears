package com.example.thriftwears.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.databinding.SavedCardViewBinding
import com.example.thriftwears.item.CardViewItem
import com.squareup.picasso.Picasso

class SavedCardViewAdapter(
    private val items: List<CardViewItem>
) : RecyclerView.Adapter<SavedCardViewAdapter.SavedCardViewHolder>() {

    inner class SavedCardViewHolder(private val binding: SavedCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardViewItem: CardViewItem) {
            binding.name.text = cardViewItem.title
            Picasso.get()
                .load(cardViewItem.image)
                .fit()
                .centerCrop()
                .tag("IMAGE_LOADING")
                .into(binding.image)

            binding.description.text = buildString {
                append(cardViewItem.description.slice(0..50))
                append("...")
            }
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

package com.example.thriftwears.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.databinding.CardViewBinding
import com.squareup.picasso.Picasso

class Adapter(
    private val items: List<Item>
) : RecyclerView.Adapter<Adapter.CardViewHolder>() {

    inner class CardViewHolder(private val binding: CardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardViewItem: Item) {
            binding.name.text = cardViewItem.title
            Picasso.get()
                .load(cardViewItem.image.toString())
                .fit()
                .centerCrop()
                .tag("IMAGE_LOADING")
                .into(binding.image)

            binding.description.text= cardViewItem.description
            binding.price.text= buildString {
                append('£')
                append(cardViewItem.price.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = CardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

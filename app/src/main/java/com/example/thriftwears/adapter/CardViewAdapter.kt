package com.example.thriftwears.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.ItemViewActivity
import com.example.thriftwears.databinding.CardViewBinding
import com.example.thriftwears.item.CardViewItemClass
import com.squareup.picasso.Picasso

class CardViewAdapter(
    private val items: List<CardViewItemClass>
) : RecyclerView.Adapter<CardViewAdapter.CardViewHolder>() {

    inner class CardViewHolder(private val binding: CardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardViewItem: CardViewItemClass) {
            binding.name.text = cardViewItem.title
            Picasso.get()
                .load(cardViewItem.image.toString())
                .fit()
                .centerCrop()
                .tag("IMAGE_LOADING")
                .into(binding.image)

            binding.price.text= buildString {
                append('£')
                append(cardViewItem.price.toString())
            }
            binding.itemId.setOnClickListener{
                val intent = Intent(binding.root.context, ItemViewActivity::class.java)
                intent.putExtra("name", cardViewItem.title)
                intent.putExtra("image", cardViewItem.image.toString())
                intent.putExtra("price", cardViewItem.price.toString())
                intent.putExtra("description", cardViewItem.description)

                binding.root.context.startActivity(intent)
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

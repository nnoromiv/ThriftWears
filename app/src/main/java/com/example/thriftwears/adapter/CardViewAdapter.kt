package com.example.thriftwears.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.CartActivity
import com.example.thriftwears.ItemViewActivity
import com.example.thriftwears.databinding.CardViewBinding
import com.example.thriftwears.item.CartItem
import com.example.thriftwears.item.ProductItem
import com.example.thriftwears.viewmodel.GlobalCartViewModel
import com.squareup.picasso.Picasso

class CardViewAdapter(
    private val items: List<ProductItem>,
    private val globalCartViewModel: GlobalCartViewModel
) : RecyclerView.Adapter<CardViewAdapter.CardViewHolder>() {

    inner class CardViewHolder(private val binding: CardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private fun showToast(message: String) = Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
        fun bind(cardViewItem: ProductItem) {
            binding.name.text = cardViewItem.title
            Picasso.get()
                .load(cardViewItem.primaryImage)
                .fit()
                .centerCrop()
                .tag("IMAGE_LOADING")
                .into(binding.image)

            binding.price.text= buildString {
                append('£')
                append(cardViewItem.price.toString())
            }
            binding.image.setOnClickListener{
                val intent = Intent(binding.root.context, ItemViewActivity::class.java)
                intent.putExtra("fileId", cardViewItem.fileId)

                binding.root.context.startActivity(intent)
            }

            binding.addToCart.setOnClickListener {
                val item = if (cardViewItem.fileId != null && cardViewItem.title != null && cardViewItem.price != null) {
                    CartItem(
                        cardViewItem.fileId!!,
                        cardViewItem.title!!,
                        cardViewItem.price!!,
                        1
                    )
                } else null

                if (item != null) {
                    globalCartViewModel.addItem(item)
                    showToast("Added to cart")
                }
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

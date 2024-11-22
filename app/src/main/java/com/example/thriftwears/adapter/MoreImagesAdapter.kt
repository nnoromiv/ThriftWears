package com.example.thriftwears.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.databinding.MoreImagesBinding
import com.example.thriftwears.item.MoreImagesItemClass
import com.squareup.picasso.Picasso

class MoreImagesAdapter(
    private val items: List<MoreImagesItemClass>
) : RecyclerView.Adapter<MoreImagesAdapter.MoreImagesHolder>() {

    inner class MoreImagesHolder(private val binding: MoreImagesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardViewItem: MoreImagesItemClass) {
            Picasso.get()
                .load(cardViewItem.image.toString())
                .fit()
                .centerCrop()
                .tag("IMAGE_LOADING")
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreImagesHolder {
        val binding = MoreImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoreImagesHolder(binding)
    }

    override fun onBindViewHolder(holder: MoreImagesHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

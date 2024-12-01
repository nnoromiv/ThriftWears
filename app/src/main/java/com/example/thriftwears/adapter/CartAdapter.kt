package com.example.thriftwears.adapter

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.thriftwears.R
import com.example.thriftwears.item.CartItem

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChanged: (Int, Int) -> Unit // Callback for quantity changes
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNameTextView: TextView = view.findViewById(R.id.itemNameTextView)
        val itemPriceTextView: TextView = view.findViewById(R.id.itemPriceTextView)
        val itemQuantityEditText: EditText = view.findViewById(R.id.itemQuantityEditText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.itemNameTextView.text = cartItem.name
        "£${cartItem.price}".also { holder.itemPriceTextView.text = it }
        holder.itemQuantityEditText.text = Editable.Factory.getInstance().newEditable(cartItem.quantity.toString())

        holder.itemQuantityEditText.doOnTextChanged { text, _, _, _ ->
            val newQuantity = text?.toString()?.toIntOrNull() ?: 1
            cartItems[position].quantity = newQuantity
            onQuantityChanged(position, newQuantity)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}

package com.nnorom.thriftwears.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nnorom.thriftwears.item.CartItem

class GlobalCartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val items: LiveData<MutableList<CartItem>> = cartItems

    fun addItem(item: CartItem) {
        val currentList = cartItems.value ?: mutableListOf()
        if (currentList.contains(item)) {
            val index = currentList.indexOf(item)
            currentList[index].quantity++
        } else {
            currentList.add(item)
            cartItems.value = currentList
        }
    }

    fun clearCart() {
        cartItems.value = mutableListOf()
    }
}
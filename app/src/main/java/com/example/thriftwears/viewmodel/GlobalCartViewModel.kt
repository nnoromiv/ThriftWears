package com.example.thriftwears.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thriftwears.item.CartItem

class GlobalCartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val items: LiveData<MutableList<CartItem>> = cartItems

    fun addItem(item: CartItem) {
        val currentList = cartItems.value ?: mutableListOf()
        currentList.add(item)
        cartItems.value = currentList
    }

    fun removeItem(item: CartItem) {
        val currentList = cartItems.value ?: mutableListOf()
        currentList.remove(item)
        cartItems.value = currentList
    }

    fun clearCart() {
        cartItems.value = mutableListOf()
    }
}
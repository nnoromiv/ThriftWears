package com.example.thriftwears

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thriftwears.card.Item
import com.example.thriftwears.card.SavedAdapter
import com.example.thriftwears.databinding.SavedBinding

class Saved : Fragment() {

    private var _binding: SavedBinding? = null
    private val binding get() = _binding!!

    private val img = Uri.parse("https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D")

    private val cardViewList = listOf(
        Item("Sweater", img, "Hurricane loves me", 200.0),
        Item("Sweater", img, "Hurricane loves me", 200.0),
        Item("Sweater", img, "Hurricane loves me", 200.0),
        Item("Sweater", img, "Hurricane loves me", 200.0),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.savedItemsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.savedItemsRecyclerView.adapter = SavedAdapter(cardViewList)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}
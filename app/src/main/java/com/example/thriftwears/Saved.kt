package com.example.thriftwears

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thriftwears.adapter.SavedCardViewAdapter
import com.example.thriftwears.databinding.SavedBinding
import com.example.thriftwears.item.CardViewItemClass

class Saved : Fragment() {

    private var _binding: SavedBinding? = null
    private val binding get() = _binding!!

    private val img = Uri.parse("https://images.unsplash.com/photo-1730727384555-35318cb80600?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxM3x8fGVufDB8fHx8fA%3D%3D")

    private val cardViewList = listOf(
        CardViewItemClass(  "item005","Sweater", img, "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Neer for glorious of fabled knew. Name him high passed little might known of a,.", 200.0),
        CardViewItemClass(  "item006", "Shirts", img, "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Neer for glorious of fabled knew. Name him high passed little might known of a,.", 400.0),
        CardViewItemClass(  "item007", "Joggers", img, "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Neer for glorious of fabled knew. Name him high passed little might known of a,.", 600.0),
        CardViewItemClass(  "item008", "Nike air", img, "Native lyres finds by on to. Land memory have it now climes delight the. To departed delight to if the ancient delight, heart stalked cell nor say, he den knew but save hall seemed such. Neer for glorious of fabled knew. Name him high passed little might known of a,.", 800.0),
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
        binding.savedItemsRecyclerView.adapter = SavedCardViewAdapter(cardViewList)

        // Add a back button listener
        binding.backButton.setOnClickListener {
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}
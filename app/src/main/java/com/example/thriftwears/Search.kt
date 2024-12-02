package com.example.thriftwears

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.thriftwears.databinding.SearchBinding
import com.example.thriftwears.viewmodel.GlobalCartViewModel
import java.util.ArrayList

class Search(
    private val globalCartViewModel: GlobalCartViewModel
) : Fragment() {

    private var _binding: SearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cartButton.setOnClickListener {
            val intent = Intent(requireContext(), CartActivity::class.java)
            val cartData = globalCartViewModel.items.value
            intent.putParcelableArrayListExtra("cart_data", cartData?.let { it1 -> ArrayList(it1) })
            requireContext().startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}
package com.example.thriftwears

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.thriftwears.databinding.UploadBinding

class Upload : Fragment() {

    private var _binding: UploadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dropdownMenu: AutoCompleteTextView = binding.upload.findViewById(R.id.dropdownMenu)
        val items = listOf("Option 1", "Option 2", "Option 3")
        val adapter = ArrayAdapter(this.requireContext(), R.layout.dropdown_item, items)
        dropdownMenu.setAdapter(adapter)

        binding.closeButton.setOnClickListener {
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}
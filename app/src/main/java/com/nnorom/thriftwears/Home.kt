package com.nnorom.thriftwears

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nnorom.thriftwears.components.SearchBar
import com.nnorom.thriftwears.databinding.HomeBinding
import com.nnorom.thriftwears.home.Chips
import com.nnorom.thriftwears.home.HomeBar
import com.nnorom.thriftwears.home.HomeBody
import com.nnorom.thriftwears.viewmodel.GlobalCartViewModel

class Home(
    private val globalCartViewModel: GlobalCartViewModel
) : Fragment() {

    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeBar = HomeBar(requireContext(), null, 0, globalCartViewModel)
        binding.homeLayout.addView(homeBar)

        val searchBar = SearchBar(requireContext(), null, 0)
        binding.homeLayout.addView(searchBar)

        val chips = Chips(requireContext(), null, 0)
        binding.homeLayout.addView(chips)

        val homeBody = HomeBody(requireContext(), null, 0, globalCartViewModel, chips, searchBar)
        binding.homeLayout.addView(homeBody)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
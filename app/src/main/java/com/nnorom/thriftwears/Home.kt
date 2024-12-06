package com.nnorom.thriftwears

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.nnorom.thriftwears.MainActivity.Companion.MAIN_ACTIVITY_KEY
import com.nnorom.thriftwears.components.SearchBar
import com.nnorom.thriftwears.databinding.HomeBinding
import com.nnorom.thriftwears.home.Chips
import com.nnorom.thriftwears.home.HomeBar
import com.nnorom.thriftwears.home.HomeBody
import com.nnorom.thriftwears.viewmodel.GlobalCartViewModel

class Home : Fragment(R.layout.home) {

    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var globalCartViewModel: GlobalCartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        globalCartViewModel = ViewModelProvider(requireActivity())[GlobalCartViewModel::class.java]
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MAIN_ACTIVITY_KEY, Home::class.java.simpleName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
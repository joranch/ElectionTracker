package com.example.android.electiontracker.ui.launch

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.electiontracker.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentLaunchBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.findRepButton.setOnClickListener { navToRepresentatives() }
        binding.electionsButton.setOnClickListener { navToElections() }

        return binding.root
    }

    private fun navToElections() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    private fun navToRepresentatives() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }

}

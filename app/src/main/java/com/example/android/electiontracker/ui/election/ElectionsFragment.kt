package com.example.android.electiontracker.ui.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.electiontracker.ElectionTrackerApplication
import com.example.android.electiontracker.databinding.FragmentElectionBinding
import com.example.android.electiontracker.ui.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ElectionsViewModel by viewModels {
        ElectionsViewModelFactory((activity?.application as ElectionTrackerApplication).database.electionDao)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        _binding = FragmentElectionBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.savedElectionsRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.savedElectionsRecyclerview.adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
        })

        binding.electionsRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.electionsRecyclerview.adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
        })

        return binding.root


        //TODO: Add binding values

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    //TODO: Refresh adapters when fragment loads

}
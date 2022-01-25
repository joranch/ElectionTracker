package com.example.android.politicalpreparedness.ui.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.ElectionTrackerApplication
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ElectionsViewModel by viewModels {
        ElectionsViewModelFactory((activity?.application as ElectionTrackerApplication).database.electionDao)
    }
//        ElectionsViewModelFactory((activity?.application as ElectionsApplication).database.electionsDao())


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        _binding = FragmentElectionBinding.inflate(layoutInflater, container, false)

//        binding.viewModel = viewModel
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
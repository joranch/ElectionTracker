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
import com.example.android.electiontracker.ui.election.ElectionsViewModel.Companion.EMPTY_SNACKBAR_INT
import com.example.android.electiontracker.ui.election.adapter.ElectionListAdapter
import com.google.android.material.snackbar.Snackbar

class ElectionsFragment: Fragment() {

    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ElectionsViewModel by viewModels {
        ElectionsViewModelFactory((activity?.application as ElectionTrackerApplication).database.electionDao)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        _binding = FragmentElectionBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSavedElectionsRecyclerview()
        setupElectionsRecyclerview()

        viewModel.showSnackbarMessage.observe(viewLifecycleOwner, {
            if(it != EMPTY_SNACKBAR_INT)
                Snackbar.make(binding.root, getText(it), Snackbar.LENGTH_SHORT).show()

            viewModel.clearSnackbarMessage()
        })

        viewModel.getElections()
    }

    private fun setupElectionsRecyclerview() {
        binding.electionsRecyclerview.layoutManager = LinearLayoutManager(context)
        val electionAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it)
            )
        })

        binding.electionsRecyclerview.adapter = electionAdapter
        binding.viewModel!!.elections.observe(viewLifecycleOwner, { items ->
            items?.let {
                electionAdapter.submitList(it)
            }
        })

    }

    private fun setupSavedElectionsRecyclerview() {
        binding.savedElectionsRecyclerview.layoutManager = LinearLayoutManager(context)
        val savedElectionsAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it)
            )
        })

        binding.savedElectionsRecyclerview.adapter = savedElectionsAdapter
        binding.viewModel!!.savedElections.observe(viewLifecycleOwner, { items ->
            items?.let {
                savedElectionsAdapter.submitList(it)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
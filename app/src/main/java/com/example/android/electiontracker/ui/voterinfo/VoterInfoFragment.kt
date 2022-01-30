package com.example.android.electiontracker.ui.voterinfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.electiontracker.ElectionTrackerApplication
import com.example.android.electiontracker.R
import com.example.android.electiontracker.databinding.FragmentVoterInfoBinding
import com.example.android.electiontracker.model.LoadingState
import com.google.android.material.snackbar.Snackbar


class VoterInfoFragment : Fragment() {

    private var _binding: FragmentVoterInfoBinding? = null
    val binding get() = _binding!!

    private val viewModel: VoterInfoViewModel by viewModels {
        VoterInfoViewModelFactory((activity?.application as ElectionTrackerApplication).database.electionDao)
    }

    val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setElection(args.argElection)
        viewModel.checkElectionIsFollowed(args.argElection)
        viewModel.loadElectionInfo(args.argElection)

        binding.followButton.setOnClickListener { followButtonOnClick() }
        binding.stateBallot.setOnClickListener { stateBallotOnClick() }
        binding.stateLocations.setOnClickListener { stateLocationOnClick() }

        viewModel.electionSaved.observe(viewLifecycleOwner, {
            setFollowButtonState(it)
        })

        viewModel.status.observe(viewLifecycleOwner, { state ->
            when (state) {
                LoadingState.DONE -> binding.followButton.isEnabled = true
                LoadingState.LOADING -> binding.followButton.isEnabled = false
            }
        })
    }

    private fun setFollowButtonState(it: Boolean) {
        when (it) {
            true -> {
                binding.followButton.text = context?.resources?.getText(R.string.unfollow_election)
            }
            else -> {
                binding.followButton.text = context?.resources?.getText(R.string.follow_election)
            }
        }
    }

    private fun stateLocationOnClick() {
        val infoUri =
            viewModel.voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
                ?: ""

        if (infoUri.isNullOrEmpty()) {
            Snackbar.make(
                binding.root,
                "Failed to open election location information",
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            launchActivityUrlIntent(infoUri)
        }
    }

    private fun stateBallotOnClick() {
        var infoUri =
            viewModel.voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
                ?: ""

        if (infoUri.isNullOrEmpty()) {
            Snackbar.make(binding.root, "Failed to open ballot information", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            launchActivityUrlIntent(infoUri)
        }
    }

    private fun followButtonOnClick() {
        viewModel.toggleFollowElection(args.argElection)
    }

    private fun launchActivityUrlIntent(urlStr: String) {
        val uri: Uri = Uri.parse(urlStr)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
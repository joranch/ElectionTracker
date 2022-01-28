package com.example.android.electiontracker.ui.voterinfo

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.electiontracker.ElectionTrackerApplication
import com.example.android.electiontracker.R
import com.example.android.electiontracker.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private var _binding: FragmentVoterInfoBinding? = null
    val binding get() = _binding!!

    private val viewModel: VoterInfoViewModel by viewModels {
        VoterInfoViewModelFactory((activity?.application as ElectionTrackerApplication).database.electionDao)
    }

    val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        return binding.root

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

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
            when (state){
                VoterInfoViewModel.LoadingState.DONE -> binding.followButton.isEnabled = true
                VoterInfoViewModel.LoadingState.LOADING -> binding.followButton.isEnabled = false
            }

        })
    }

    private fun setFollowButtonState(it: Boolean) {
        when(it) {
            true -> { binding.followButton.text = context?.resources?.getText(R.string.unfollow_election)}
            else -> { binding.followButton.text = context?.resources?.getText(R.string.follow_election) }
        }
    }

    private fun stateLocationOnClick() {
        TODO("Not yet implemented")
    }

    private fun stateBallotOnClick() {
        TODO("Not yet implemented")
    }

    private fun followButtonOnClick() {
        viewModel.toggleFollowElection(args.argElection)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //TODO: Create method to load URL intents

}
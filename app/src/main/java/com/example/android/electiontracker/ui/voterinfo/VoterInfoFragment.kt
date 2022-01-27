package com.example.android.electiontracker.ui.voterinfo

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.electiontracker.ElectionTrackerApplication
import com.example.android.electiontracker.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private var _binding: FragmentVoterInfoBinding? = null
    val binding get() = _binding!!

    private val viewModel: VoterInfoViewModel by viewModels {
        VoterInfoViewModelFactory((activity?.application as ElectionTrackerApplication).database.electionDao)
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        return binding.root
        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

    }

    //TODO: Create method to load URL intents

}
package com.example.android.electiontracker.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.electiontracker.R
import com.example.android.electiontracker.network.models.VoterInfoResponse

@BindingAdapter("electionInfoTitle")
fun bindElectionInfoTitleText(view: TextView, voterInfo: VoterInfoResponse?) {
    voterInfo?.run {
        view.text = view.resources.getString(R.string.election_info_text, state?.get(0)?.name)
    }
}
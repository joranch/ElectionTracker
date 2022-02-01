package com.example.android.electiontracker.model

import android.os.Parcelable
import com.example.android.electiontracker.network.models.Office
import com.example.android.electiontracker.network.models.Official
import kotlinx.parcelize.Parcelize

@Parcelize
data class Representative (
        val official: Official,
        val office: Office
) : Parcelable
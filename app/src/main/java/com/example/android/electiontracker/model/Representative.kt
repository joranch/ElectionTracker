package com.example.android.electiontracker.model

import com.example.android.electiontracker.network.models.Office
import com.example.android.electiontracker.network.models.Official

data class Representative (
        val official: Official,
        val office: Office
)
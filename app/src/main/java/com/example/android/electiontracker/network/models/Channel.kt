package com.example.android.electiontracker.network.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Channel (
    val type: String,
    val id: String
) : Parcelable
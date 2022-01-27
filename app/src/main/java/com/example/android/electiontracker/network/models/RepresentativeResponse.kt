package com.example.android.electiontracker.network.models

data class RepresentativeResponse(
        val offices: List<Office>,
        val officials: List<Official>
)
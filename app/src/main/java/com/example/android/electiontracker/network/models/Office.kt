package com.example.android.electiontracker.network.models

import com.example.android.electiontracker.model.Representative
import com.squareup.moshi.Json

data class Office (
    val name: String,
    @Json(name="divisionId") val division:Division,
    @Json(name="officialIndices") val officials: List<Int>
) {
    fun getRepresentatives(officials: List<Official>): List<Representative> {
        return this.officials.map { index ->
            Representative(officials[index], this)
        }
    }
}

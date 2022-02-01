package com.example.android.electiontracker.network.models

import android.os.Parcelable
import com.example.android.electiontracker.model.Representative
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Office (
    val name: String,
    @Json(name="divisionId") val division:Division,
    @Json(name="officialIndices") val officials: List<Int>
) : Parcelable {
    fun getRepresentatives(officials: List<Official>): List<Representative> {
        return this.officials.map { index ->
            Representative(officials[index], this)
        }
    }
}

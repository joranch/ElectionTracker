package com.example.android.electiontracker.network.jsonadapter

import android.util.Log
import com.example.android.electiontracker.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class ElectionAdapter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @FromJson
    fun divisionFromJson (ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val districtDelimiter = "district:"

        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
                .substringBefore("/")

        val state = if(ocdDivisionId.contains(districtDelimiter)){
            ocdDivisionId.substringAfter(districtDelimiter,"")
                .substringBefore("/")
        } else {
            ocdDivisionId.substringAfter(stateDelimiter,"")
                .substringBefore("/")
        }

        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson (division: Division): String {
        return division.id
    }

    @FromJson
    fun dateFromJson(dateStr: String): Date? {
        return dateFormat.parse(dateStr)
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return dateFormat.format(date)
    }
}
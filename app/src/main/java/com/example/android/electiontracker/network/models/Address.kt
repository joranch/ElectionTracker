package com.example.android.electiontracker.network.models

data class Address (
    val line1: String,
    val line2: String? = null,
    val city: String,
    var state: String,
    val zip: String
) {
    fun toFormattedString(): String {
        var output = line1.plus("\n")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus("\n")
        output = output.plus("$city, $state $zip")
        return output
    }
}
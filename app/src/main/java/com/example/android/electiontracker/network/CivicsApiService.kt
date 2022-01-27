package com.example.android.electiontracker.network

import android.util.Log
import com.example.android.electiontracker.network.jsonadapter.ElectionAdapter
import com.example.android.electiontracker.network.models.Election
import com.example.android.electiontracker.network.models.ElectionResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

// TODO: Add adapters for Java Date and custom adapter ElectionAdapter (included in project)
private val moshi = Moshi.Builder()
    .add(ElectionAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(CivicsHttpClient.getClient())
    .baseUrl(BASE_URL)
    .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

private interface CivicsApiService {

    @GET("elections")
    suspend fun getElections() : ElectionResponse

    @GET("voterinfo")
    suspend fun getVoterInfo()

    //TODO: Add representatives API Call
    @GET("representatives")
    suspend fun getRepresentativesByLocation(address: String)
}

object CivicsApi {
    private val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }

    suspend fun getElections() : ElectionResponse{
        return retrofitService.getElections()
    }
}
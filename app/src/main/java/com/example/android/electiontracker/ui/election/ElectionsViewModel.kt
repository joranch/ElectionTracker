package com.example.android.electiontracker.ui.election

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.electiontracker.database.ElectionDao
import com.example.android.electiontracker.network.CivicsApi
import kotlinx.coroutines.launch


//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(val electionDao: ElectionDao): ViewModel() {
    companion object {
        const val TAG = "ElectionsViewModel"
    }
    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info
    init {
        getElections()
    }

    fun getElections() {
        viewModelScope.launch {
            try {
                val response = CivicsApi.getElections()

            } catch (e: Throwable){
                Log.e(TAG, e.localizedMessage)
                e.printStackTrace()
            }
        }
    }
}
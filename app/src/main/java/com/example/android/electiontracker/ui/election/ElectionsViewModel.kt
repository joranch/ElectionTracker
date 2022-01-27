package com.example.android.electiontracker.ui.election

import android.util.Log
import androidx.lifecycle.*
import com.example.android.electiontracker.database.ElectionDao
import com.example.android.electiontracker.network.CivicsApi
import com.example.android.electiontracker.network.models.Election
import kotlinx.coroutines.launch


//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(val electionDao: ElectionDao): ViewModel() {
    companion object {
        const val TAG = "ElectionsViewModel"
    }
    //TODO: Create live data val for upcoming elections
    private var _elections = MutableLiveData<List<Election>>()
    val elections: LiveData<List<Election>> = _elections

    val savedElections: LiveData<List<Election>> = electionDao.getElections().asLiveData()

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info
    init {
        getElections()
    }

    private fun getElections() {
        viewModelScope.launch {
            try {
                val response = CivicsApi.getElections()

                _elections.value = response.elections

            } catch (e: Throwable){
                Log.e(TAG, e.localizedMessage)
                e.printStackTrace()
            }
        }
    }
}
package com.example.android.electiontracker.ui.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.electiontracker.R
import com.example.android.electiontracker.database.ElectionDao
import com.example.android.electiontracker.network.CivicsApi
import com.example.android.electiontracker.network.jsonadapter.ElectionAdapter
import com.example.android.electiontracker.network.models.Election
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch


//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(val electionDao: ElectionDao): ViewModel() {
    companion object {
        const val TAG = "ElectionsViewModel"
        const val EMPTY_SNACKBAR_INT = 0
    }
    //TODO: Create live data val for upcoming elections
    private var _elections = MutableLiveData<List<Election>>()
    val elections: LiveData<List<Election>> = _elections

    val savedElections: LiveData<List<Election>> = electionDao.getElections().asLiveData()

    private var _showSnackbarMessage = MutableLiveData<Int>()
    val showSnackbarMessage: LiveData<Int> = _showSnackbarMessage

    fun getElections() {
        viewModelScope.launch {
            try {
                val response = CivicsApi.getElections()

                _elections.value = response.elections

            } catch (e: Throwable){
                Log.e(TAG, e.localizedMessage)
                e.printStackTrace()
                _showSnackbarMessage.value = R.string.error_loading_elections
            }
        }
    }

    fun clearSnackbarMessage() {
        _showSnackbarMessage.value = EMPTY_SNACKBAR_INT
    }
}
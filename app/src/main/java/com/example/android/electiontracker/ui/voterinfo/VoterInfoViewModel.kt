package com.example.android.electiontracker.ui.voterinfo

import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.electiontracker.database.ElectionDao
import com.example.android.electiontracker.network.CivicsApi
import com.example.android.electiontracker.network.models.Election
import com.example.android.electiontracker.network.models.VoterInfoResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    companion object {
        const val TAG = "VoterInfoViewModel"
        const val DEFAULT_ADDRESS = "ny"
    }

    enum class LoadingState{
        LOADING,
        ERROR,
        DONE
    }

    private var _election = MutableLiveData<Election>()
    val election : LiveData<Election> = _election

    private var _electionSaved = MutableLiveData<Boolean>()
    val electionSaved: LiveData<Boolean> = _electionSaved

    private var _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse> = _voterInfo

    private var _status = MutableLiveData<LoadingState>()
    val status: LiveData<LoadingState> = _status

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

//    init {
//        checkElectionIsFollowed()
//    }

    fun loadElectionInfo(election: Election) {
//        _election.value = election
//        _status.value = LoadingState.LOADING

        viewModelScope.launch {
            try {
                val state = when(election.division.state.isEmpty()) {
                    true -> DEFAULT_ADDRESS
                    else -> election.division.state
                }

                val address = "${state},${election.division.country}"

                _election.value?.let {
                    Log.i(TAG, "Fetching voter info: address $address electionId: ${election.id}")
                    _voterInfo.value = CivicsApi.getVoterInfo(address, it.id)
                }

            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e(TAG, e.localizedMessage)
            } finally {
//                _status.value = LoadingState.DONE
            }
        }
    }

    fun checkElectionIsFollowed(election: Election) {
        _status.value = LoadingState.LOADING

        viewModelScope.launch {
            try {
                val electionIsFollowed = dataSource.get(election.id)

                _electionSaved.value = electionIsFollowed != null
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e(TAG, e.localizedMessage)
            }
            finally {
                _status.value = LoadingState.DONE
            }
        }
    }

    fun toggleFollowElection(election: Election) {
        _status.value = LoadingState.LOADING
        viewModelScope.launch {
            try {
                if(electionSaved.value == false){
                    dataSource.insert(election)
                    _electionSaved.value = true
                } else {
                    dataSource.delete(election)
                    _electionSaved.value = false
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e(TAG, e.localizedMessage)
            } finally {
                _status.value = LoadingState.DONE
            }
        }

    }

    fun setElection(election: Election) {
        _election.value = election
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}
package com.example.android.electiontracker.ui.representative

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.example.android.electiontracker.R
import com.example.android.electiontracker.model.Representative
import com.example.android.electiontracker.network.CivicsApi
import com.example.android.electiontracker.network.models.Address
import com.example.android.electiontracker.ui.election.ElectionsViewModel
import kotlinx.coroutines.launch


class RepresentativeViewModel(private var savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        const val TAG = "RepresentativeViewModel"
        const val STATE_NOT_SELECTED = 0

        const val ADDRESS_KEY = "address_key"
        const val REPRESENTATIVES_KEY = "reps_key"
        const val MOTION_LAYOUT_STATE_KEY = "motion_layout_state_id"
    }

    val representatives: LiveData<List<Representative>> = savedStateHandle.getLiveData(
        REPRESENTATIVES_KEY
    )

    val address: LiveData<Address> = savedStateHandle.getLiveData(ADDRESS_KEY)

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>> = _states

    private var _showSnackbarMessage = MutableLiveData<Int>()
    val showSnackbarMessage: LiveData<Int> = _showSnackbarMessage

    val selectedStateIndex = MutableLiveData<Int>(0)

    init {
        if (savedStateHandle.get<Address>(ADDRESS_KEY) == null) {
            savedStateHandle.set(ADDRESS_KEY, Address("", "", "", "", ""))
        }
        selectedStateIndex.value = getSelectedAddressStateIndex()

    }

    fun setAddress(address: Address) {
        savedStateHandle.set(ADDRESS_KEY, address)
        selectedStateIndex.value = getSelectedAddressStateIndex()
    }

    fun setRepresentativesList(list: List<Representative>) {
        savedStateHandle.set(REPRESENTATIVES_KEY, list)
    }

    fun setStates(states: List<String>) {
        _states.value = states
    }

    fun getRepresentatives() {
        viewModelScope.launch {
            try {
                savedStateHandle.get<Address>(ADDRESS_KEY)?.state =
                    getSelectedState(selectedStateIndex.value!!)

                val addressStr = address.value!!.toFormattedString()
                Log.e(TAG, addressStr)

                val representativeResponse = CivicsApi.getRepresentatives(addressStr)

                val representativeList = representativeResponse.offices.flatMap { office ->
                    office.getRepresentatives(representativeResponse.officials)
                }

                setRepresentativesList(representativeList)
            } catch (e: Exception) {
                _showSnackbarMessage.value = R.string.error_representative_search
                e.printStackTrace()
            }
        }
    }

    fun clearSnackbarMessage() {
        _showSnackbarMessage.value = ElectionsViewModel.EMPTY_SNACKBAR_INT
    }

    private fun getSelectedState(stateIndex: Int): String {
        return states.value?.get(stateIndex) ?: ""
    }

    private fun getSelectedAddressStateIndex(): Int {
        return states.value?.indexOf(address.value?.state) ?: STATE_NOT_SELECTED
    }
}

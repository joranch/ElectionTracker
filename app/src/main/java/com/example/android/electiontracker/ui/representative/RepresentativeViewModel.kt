package com.example.android.electiontracker.ui.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.electiontracker.R
import com.example.android.electiontracker.model.LoadingState
import com.example.android.electiontracker.model.Representative
import com.example.android.electiontracker.network.CivicsApi
import com.example.android.electiontracker.network.models.Address
import com.example.android.electiontracker.ui.election.ElectionsViewModel
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    companion object {
        const val TAG = "RepresentativeViewModel"
        const val STATE_NOT_SELECTED = 0
    }

    private var _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> = _representatives

    private var _address = MutableLiveData<Address>()
    val address: LiveData<Address> = _address

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>> = _states

    private var _showSnackbarMessage = MutableLiveData<Int>()
    val showSnackbarMessage: LiveData<Int> = _showSnackbarMessage

    fun setAddress(address: Address) {
        _address.value = address
    }

    fun setStates(states: List<String>) {
        _states.value = states
    }

    fun getSelectedAddressStateIndex() : Int {
        return states.value?.indexOf(address.value?.state) ?: STATE_NOT_SELECTED
    }

    fun getRepresentatives(address: Address) {
        viewModelScope.launch {
            try {
                val addressStr = address.toFormattedString()
                val representativeResponse = CivicsApi.getRepresentatives(addressStr)

                val representativeList = representativeResponse.offices.flatMap { office ->
                    office.getRepresentatives(representativeResponse.officials)
                }

                _representatives.value = representativeList
            } catch (e: Throwable) {
                _showSnackbarMessage.value = R.string.error_representative_search
                e.printStackTrace()
            }
        }
    }

    fun clearSnackbarMessage() {
        _showSnackbarMessage.value = ElectionsViewModel.EMPTY_SNACKBAR_INT
    }

    private fun getSelectedState(stateIndex: Int): String {
        return states.value!!.toList()[stateIndex]
    }

    fun createAndSetAddress(
        line1: String,
        line2: String,
        city: String,
        zip: String,
        stateIndex: Int
    ): Address {
        val address = Address(
            line1, line2, city, getSelectedState(stateIndex), zip
        )

        _address.value = address
        return address

    }
}

package com.example.android.electiontracker.ui.representative

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
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

    val selectedStateIndex = MutableLiveData<Int>(0)

    init {
        _address.value = Address("", "","","","")
    }

    fun setAddress(address: Address) {
        _address.value = address
        selectedStateIndex.value = getSelectedAddressStateIndex()
    }

    fun setStates(states: List<String>) {
        _states.value = states
    }

    fun getRepresentatives() {
        viewModelScope.launch {
            try {
                _address.value?.state = getSelectedState(selectedStateIndex.value!!)
                val addressStr = address.value!!.toFormattedString()
                Log.e(TAG, addressStr)
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

//    fun createAndSetAddress(
//        line1: String,
//        line2: String,
//        city: String,
//        zip: String,
//        stateIndex: Int
//    ): Address {
//        val address = Address(
//            line1, line2, city, getSelectedState(stateIndex), zip
//        )
//
//        _address.value = address
//        return address
//
//    }

    private fun getSelectedState(stateIndex: Int): String {
        return states.value?.get(stateIndex) ?: ""
    }

    private fun getSelectedAddressStateIndex() : Int {
        return states.value?.indexOf(address.value?.state) ?: STATE_NOT_SELECTED
    }
}

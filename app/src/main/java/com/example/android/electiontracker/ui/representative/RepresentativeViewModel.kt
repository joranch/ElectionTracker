package com.example.android.electiontracker.ui.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.electiontracker.model.Representative
import com.example.android.electiontracker.network.CivicsApi
import com.example.android.electiontracker.network.models.Address
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    companion object {
        const val TAG = "RepresentativeViewModel"
        const val STATE_NOT_SELECTED = 0
    }

    //TODO: Establish live data for representatives and address
    private var _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> = _representatives

    private var _address = MutableLiveData<Address>()
    val address: LiveData<Address> = _address

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>> = _states

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

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
                e.printStackTrace()
            }
        }
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

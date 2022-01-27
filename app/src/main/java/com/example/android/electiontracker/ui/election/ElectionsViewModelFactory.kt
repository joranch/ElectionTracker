package com.example.android.electiontracker.ui.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.electiontracker.database.ElectionDao

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(private val electionDao: ElectionDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(electionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }

}
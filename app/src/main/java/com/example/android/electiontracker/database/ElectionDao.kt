package com.example.android.electiontracker.database

import androidx.room.Dao
import androidx.room.Insert
import com.example.android.electiontracker.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert
    suspend fun insert(election: Election)

    //TODO: Add select all election query

    //TODO: Add select single election query

    //TODO: Add delete query

    //TODO: Add clear query

}
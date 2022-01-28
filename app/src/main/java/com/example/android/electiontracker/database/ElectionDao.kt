package com.example.android.electiontracker.database

import androidx.room.*
import com.example.android.electiontracker.network.models.Election
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(elections: List<Election>)

    @Update
    suspend fun update(election: Election)

    @Query("SELECT * FROM election_table ORDER BY electionDay")
    fun getElections() : Flow<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun get(id: Int) : Election?

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election_table")
    suspend fun clearElections()

}
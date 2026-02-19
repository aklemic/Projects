package com.example.bingetracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WatchEntryDao {

    @Query("SELECT * FROM watch_entries WHERE titleId = :titleId ORDER BY id")
    fun getEntriesForTitle(titleId: Long): LiveData<List<WatchEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WatchEntry): Long

    @Update
    suspend fun updateEntry(entry: WatchEntry)

    @Delete
    suspend fun deleteEntry(entry: WatchEntry)
}

package com.example.bingetracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TitleDao {

    @Query("SELECT * FROM titles ORDER BY name")
    fun getAllTitles(): LiveData<List<Title>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitle(title: Title): Long

    @Update
    suspend fun updateTitle(title: Title)

    @Delete
    suspend fun deleteTitle(title: Title)
}

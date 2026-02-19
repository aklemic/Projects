package com.example.bingetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "titles")
data class Title(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val type: String,
    val status: String,
    val rating: Int? = null
)

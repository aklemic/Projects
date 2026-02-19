package com.example.bingetracker

import androidx.lifecycle.LiveData

class TitleRepository(private val titleDao: TitleDao) {

    val allTitles: LiveData<List<Title>> = titleDao.getAllTitles()

    suspend fun insert(title: Title): Long {
        return titleDao.insertTitle(title)
    }

    suspend fun update(title: Title) {
        titleDao.updateTitle(title)
    }

    suspend fun delete(title: Title) {
        titleDao.deleteTitle(title)
    }
}

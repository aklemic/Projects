package com.example.bingetracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TitleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TitleRepository
    val allTitles: LiveData<List<Title>>

    init {
        val db = BingeDatabase.getInstance(application)
        val titleDao = db.titleDao()
        repository = TitleRepository(titleDao)
        allTitles = repository.allTitles
    }

    fun insert(title: Title) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(title)
        }
    }

    fun update(title: Title) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(title)
        }
    }

    fun delete(title: Title) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(title)
        }
    }
}

package com.example.flushit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.flushit.model.*

class UserToiletsViewModel(private val repository: Repository) : ViewModel() {


    fun getUserToilets(): LiveData<List<Rating>> = repository.getToiletsRatedByUser()

    fun removeToiletFromUser(toiletToRemove: Toilet, grade: Float) = repository.removeToiletFromUser(toiletToRemove, grade)
}
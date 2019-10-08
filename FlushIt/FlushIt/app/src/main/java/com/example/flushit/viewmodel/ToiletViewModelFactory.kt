package com.example.flushit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flushit.model.Repository

/**
 * Viewmodel factory for the toilet viewmodel.
 * @param repository : The repository that the toilet viewmodel will need.
 */
class ToiletViewModelFactory(private val repository: Repository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T{
        return ToiletsViewModel(repository) as T
    }
}
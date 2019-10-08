package com.example.flushit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flushit.model.Repository
import java.lang.IllegalArgumentException

/**
 * Viewmodel factory for the user viewmodel. This factory is designed as a singleton as there are
 * many activities that need to share the same viewmodel.
 * @param repository : The repository that the user viewmodel needs.
 */
@Suppress("UNCHECKED_CAST")
class UserViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory{

    /**
     * If the requested viewmodel already is in the hashmap, return the existing viewmodel.
     * Otherwise, add the viewmodel in the hashmap and return it afterwards. If it's not
     * a user viewmodel that is requested, an IllegalArgumentException is thrown.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java)){
            val key = "UserViewModel"
            if(hashMapViewModel.containsKey(key)){
                return getViewModel(key) as T
            }else{
                addViewModel(
                    key,
                    UserViewModel(repository)
                )
                return getViewModel(key) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


    /**
     * The HashMap that contains the viewmodel that is created.
     */
    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()
        fun addViewModel(key: String, viewModel: ViewModel){
            hashMapViewModel.put(key, viewModel)
        }
        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }

}
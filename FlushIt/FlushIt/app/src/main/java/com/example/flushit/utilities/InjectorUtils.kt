package com.example.flushit.utilities

import com.example.flushit.viewmodel.ToiletViewModelFactory
import com.example.flushit.viewmodel.UserViewModelFactory
import com.example.flushit.model.Database
import com.example.flushit.model.Repository

/**
 * Singleton class that is used to create viewmodel factories. This is the only place where the repository and
 * database classes are created.
 */
object InjectorUtils {

    fun provideToiletsViewModelFactory() : ToiletViewModelFactory {
        val repository = Repository.getInstance(Database.getInstance())
        return ToiletViewModelFactory(repository)
    }

    fun provideUserViewModelFactor() : UserViewModelFactory {
        val repository = Repository.getInstance(Database.getInstance())
        return UserViewModelFactory(repository)
    }
}
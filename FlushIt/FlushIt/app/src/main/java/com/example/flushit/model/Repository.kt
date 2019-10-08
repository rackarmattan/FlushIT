package com.example.flushit.model

import android.net.Uri

/**
 * Connection between the viewmodels and the data access objects.
 * @param toiletDAO : The toilet data access object
 * @param userDAO : The user data access object
 */
class Repository private constructor(private val database: Database) {

    /**
     * Singleton class but with parameters in the constructor.
     */
    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(database: Database) =
            instance ?: synchronized(this) {
                instance ?: Repository(database).also { instance = it }
            }
    }

    /**
     * Update a toilet in the DAO.
     * @param toilet : The toilet to update
     */
    fun updateToilet(toilet:Toilet){
        database.toiletDao.updateToilet(toilet)
    }

    /**
     * Upload a toilet in the DAO.
     * @param toilet : The toilet to update
     * @param imageUri : The image that belongs to the toilet
     * @param fileExtension :  The image's file extension
     */
    fun uploadToilet(toilet:Toilet, fileExtension: String, imageUri: Uri){
        database.toiletDao.uploadToilet(toilet, fileExtension, imageUri)
    }

    /**
     * Add a user in the DAO.
     * @param user : The user to add
     */
    fun addUser(user:User){
        database.userDao.addUser(user)
    }

    /**
     * Update a user in the DAO.
     * @param user : The user to update
     */
    fun updateUser(user:User){
        database.userDao.updateUser(user)
    }

    /**
     * Get the toilets from the DAO.
     * @return toilets from DAO
     */
    fun getToilets() = database.toiletDao.getToilets()

    /**
     * Get the current user from the DAO.
     * @return the current user from DAO
     */
    fun getCurrentUser() = database.userDao.getCurrentUser()
}
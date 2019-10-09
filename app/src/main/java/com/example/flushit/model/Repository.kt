package com.example.flushit.model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

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
    fun updateToilet(toilet: Toilet) {
        database.toiletDao.updateToilet(toilet)
    }

    /**
     * Upload a toilet in the DAO.
     * @param toilet : The toilet to update
     * @param imageUri : The image that belongs to the toilet
     * @param fileExtension :  The image's file extension
     */
    fun uploadToilet(toilet: Toilet, fileExtension: String, imageUri: Uri) {
        database.toiletDao.uploadToilet(toilet, fileExtension, imageUri)
    }

    /**
     * Add a user in the DAO.
     * @param user : The user to add
     */
    fun addUser(user: User) {
        database.userDao.addUser(user)
    }

    /**
     * Update a user in the DAO.
     * @param user : The user to update
     */
    fun updateUser(user: User) {
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


    //--------------------nytillskott-----------------------------------------

    fun deleteUsser(user:User) = database.userDao.deleteUser(user)


    fun rateToilet(userThatRated: User, toiletToRate: Toilet, grade: Float) {
        database.ratingDAO.rateToilet(userThatRated, toiletToRate, grade)
    }

    fun removeToiletFromUser(owningUser: User, toiletToRemove: Toilet, grade: Float) {
        removeRatingFromToilet(toiletToRemove, grade)
        database.ratingDAO.removeToiletFromUser(owningUser, toiletToRemove)
    }

    private fun removeRatingFromToilet(toilet: Toilet, grade: Float) {
        with(toilet) {
            if (totalRating - grade >= 0 && nrOfRaters - 1 >= 0) {
                totalRating -= grade
                nrOfRaters -= 1
                averageRating = totalRating / nrOfRaters

            } else {
                totalRating = 0f
                nrOfRaters = 0
                averageRating = 0f
            }
        }
        updateToilet(toilet)
    }

    fun getToiletsRatedByUser(user: User) = database.ratingDAO.getToiletsRatedByUser(user)

    fun getUsersWhoRatedToilet(toilet: Toilet) = database.ratingDAO.getUsersWhoRatedToilet(toilet)

    fun getToiletsRatedByUser(): LiveData<List<Rating>>{
        return Transformations.switchMap(DoubleTriggerLiveData(getCurrentUser(), getToilets())){
            //TODO gör så här för nu, ändra toiletDAO från dsnapshot till toilet list?
            val toiletList = mutableListOf<Toilet>()
            it.second?.let {
                for(snapshot in it.children){
                    val tmp = snapshot.getValue(Toilet::class.java)
                    tmp?.let { toiletList.add(tmp) }
                }
            }
            it.first?.let { database.ratingDAO.getToiletsRatedByUser(it, toiletList) }
        }
    }

    fun removeToiletFromUser(toiletToRemove: Toilet, grade: Float) {
        removeRatingFromToilet(toiletToRemove, grade)
        Transformations.map(getCurrentUser()){
            //TODO tror att detta inte körs eftersom ingen observerar getCurrentUser när denna körs i toilet adapter?
            /*
            Lösning: Kanske observera user i viewmodelen?
             */
            database.ratingDAO.removeToiletFromUser(it, toiletToRemove)
        }
    }
}
package com.example.flushit.model

import androidx.lifecycle.LiveData
import com.google.firebase.database.FirebaseDatabase

class RatingDAO : IRatingDAO {
    private var userRatingDatabaseRef = FirebaseDatabase.getInstance().getReference("users_rankings")
    private var toiletRatingDatabaseRef = FirebaseDatabase.getInstance().getReference("toilets_rankings")
    private var currentUserRatingDatabaseLiveData = FirebaseToiletListLiveData(userRatingDatabaseRef)

    override fun rateToilet(userThatRated: User, toiletToRate: Toilet, grade: Float) {
        val userID = userThatRated.uid
        val toiletID = toiletToRate.id
        userID?.let {
            toiletID?.let {
                userRatingDatabaseRef.child(userID).child(it).setValue(grade)
                toiletRatingDatabaseRef.child(it).child(userID).setValue(grade)
            }
        }
    }

    override fun removeToiletFromUser(owningUser: User, toiletToRemove: Toilet) {
        val userID = owningUser.uid
        val toiletID = toiletToRemove.id
        userID?.let {
            toiletID?.let {
                userRatingDatabaseRef.child(userID).child(it).removeValue()
                toiletRatingDatabaseRef.child(it).child(userID).removeValue()
            }
        }
    }

    override fun getToiletsRatedByUser(user: User): LiveData<List<Rating>>? {
        return user.uid?.let {
            currentUserRatingDatabaseLiveData.reference = userRatingDatabaseRef.child(it)
            currentUserRatingDatabaseLiveData
        }
    }


    override fun getUsersWhoRatedToilet(toilet: Toilet): LiveData<List<User>>? {
        return toilet.id?.let {
            FirebaseUserListLiveData(toiletRatingDatabaseRef.child(it))
        }
    }

    //TEST--------------------------- 12/9

    override fun getToiletsRatedByUser(user: User, allToilets: List<Toilet>): LiveData<List<Rating>>? {
        return user.uid?.let {
            currentUserRatingDatabaseLiveData.reference = userRatingDatabaseRef.child(it)
            currentUserRatingDatabaseLiveData.setAllToilets(allToilets)
            currentUserRatingDatabaseLiveData
        }
    }
}
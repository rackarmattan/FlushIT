package com.example.flushit.model

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot

interface IRatingDAO {

    fun rateToilet(userThatRated: User, toiletToRate: Toilet, grade: Float)
    fun removeToiletFromUser(owningUser: User, toiletToRemove: Toilet)
    //fun getToiletsRatedByUser(user: User): LiveData<List<Toilet>>?
    fun getToiletsRatedByUser(user: User): LiveData<List<Rating>>?
    fun getUsersWhoRatedToilet(toilet:Toilet): LiveData<List<User>>?

    fun getToiletsRatedByUser(user: User, allToilets: List<Toilet>): LiveData<List<Rating>>?


}
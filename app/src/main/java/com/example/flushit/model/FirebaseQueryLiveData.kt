package com.example.flushit.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * Class taken from https://firebase.googleblog.com/2017/12/using-android-architecture-components.html but
 * changed from extending LiveData<DataSnapshot> to MutableLiveData<DataSnapshot> in order to be able
 * to set new data.
 *
 * Used to listen to changes in the database but with the functionality of the MutableLiveData class.
 * @param reference : The path in the database to listen to changes on.
 */

class FirebaseQueryLiveData(val reference: DatabaseReference) : MutableLiveData<DataSnapshot>() {
    private val LOG_TAG = "FirebaseQueryLiveData"
    private val listener = MyValueEventListener()

    override fun onActive() {
        super.onActive()
        Log.d(LOG_TAG, "onActive")
        reference.addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        Log.d(LOG_TAG, "onInactive")
        reference.removeEventListener(listener)
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            value = dataSnapshot
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Can't listen to query $reference", databaseError.toException())
        }

    }
}
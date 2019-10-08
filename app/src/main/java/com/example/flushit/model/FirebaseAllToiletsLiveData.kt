package com.example.flushit.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FirebaseAllToiletsLiveData(val reference: DatabaseReference) : MutableLiveData<List<Toilet>>() {
    private val LOG_TAG = "FBAllToiletLiveData"
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
            val toilets = mutableListOf<Toilet>()
            for(snapShot in dataSnapshot.children){
                val tmp = snapShot.getValue(Toilet::class.java)
                tmp?.let { toilets.add(it) }
            }
            value = toilets
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Can't listen to query $reference", databaseError.toException())
        }

    }
}
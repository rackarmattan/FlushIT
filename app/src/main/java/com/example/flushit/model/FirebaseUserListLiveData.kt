package com.example.flushit.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FirebaseUserListLiveData(val reference: DatabaseReference) : MutableLiveData<List<User>>() {
    private val LOG_TAG = "FirebaseUserListLive"
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
            val userList = mutableListOf<User>()
            for(user in dataSnapshot.children){
                println("user snapshot " + user.value)
                //userList.add(user.value as User)
            }
            value = userList
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Can't listen to query $reference", databaseError.toException())
        }

    }
}
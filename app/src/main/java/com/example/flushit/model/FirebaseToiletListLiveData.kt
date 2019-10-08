package com.example.flushit.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class FirebaseToiletListLiveData(var reference: DatabaseReference) : MutableLiveData<List<Rating>>(), AllToiletsChangedListener{ //MutableLiveData<List<Toilet>>() {
    private val LOG_TAG = "FirebaseToiletListLive"
    //private val listenerForAllToilets = AllToiletValueListener()
    private val listenerForUserToilets = UserToiletValueListener()
    //private val toiletDatabaseRef = FirebaseDatabase.getInstance().getReference("toilets")
    private var allToilets = mutableListOf<Toilet>()

    override fun onActive() {
        super.onActive()
        Log.d(LOG_TAG, "onActive")
        reference.addValueEventListener(listenerForUserToilets)
        //toiletDatabaseRef.addValueEventListener(listenerForAllToilets)
    }

    override fun onInactive() {
        super.onInactive()
        Log.d(LOG_TAG, "onInactive")
        reference.removeEventListener(listenerForUserToilets)
        //toiletDatabaseRef.removeEventListener(listenerForAllToilets)
    }

    fun setAllToilets(toilets: List<Toilet>){
        allToilets = toilets.toMutableList()
    }

    override fun onAllToiletsChanged(toilets : List<Toilet>) {
        setAllToilets(toilets)
    }

    /*
    private inner class AllToiletValueListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            allToilets.clear()
            for (toilet in dataSnapshot.children){
                val tmpToilet = toilet.getValue(Toilet::class.java)
                tmpToilet?.let { allToilets.add(it) }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Can't listen to query $toiletDatabaseRef", databaseError.toException())
        }
    }*/

    private inner class UserToiletValueListener : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val userToilets = mutableListOf<Toilet>()
            val ratings = mutableListOf<Rating>()
            for(toiletID in dataSnapshot.children){
                println("STOOORLEK!!!! ${allToilets.size}")
                for (toilet in allToilets){
                    if(toiletID.key.equals(toilet.id)){
                        userToilets.add(toilet)
                        val rate = Rating()
                        rate.toilet = toilet
                        (toiletID.getValue(Float::class.java))?.let { rate.grade = it }
                        println("INSIDE FOR LOOP " + rate.toilet?.name)
                        ratings.add(rate)
                    }
                }
            }
            value = ratings
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Can't listen to query $reference", databaseError.toException())
        }
    }
}
package com.example.flushit.model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/**
 * The data access object for toilets. Holds the reference from Firebase to the toilets place in the database.
 * As the toilets have pictures, there is both a StorageReference and a DatabaseReference.
 */
class ToiletDAO {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("toilets")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("toilets")
    private val toilets = FirebaseQueryLiveData(databaseReference)

    /**
     * @return All the toilets as LiveData containing the snapshot from Firebase. As LiveData so no other
     * class can mutate the data.
     */
    fun getToilets(): LiveData<DataSnapshot> {
        return toilets
    }

    /**
     * Takes a toilet and with the toilet's id it's updated in the Firebase database.
     * @param toilet : The toilet to update in the database.
     */
    fun updateToilet(toilet: Toilet){
        try {
            toilet.id?.let { databaseReference.child(it).setValue(toilet) }
        }catch (exception: DatabaseException){
            println("KAOS I TOILET DAO " + exception.message)
        }
    }

    /**
     * Upload a new toilet in the database. Gets a toilet, an iamge and the image's file extension.
     * First, this function tries to put the image in the storage at Firebase. If it was successful,
     * a new toilet object is created in the DatabaseReference. As this isn't something a regular user
     * will do, the errors are not handled as they should be if that was the case.
     */
    fun uploadToilet(toilet:Toilet, fileExtension: String, imageUri: Uri){
        val fileReference = storageReference.child(System.currentTimeMillis().toString() + "." + fileExtension)
        fileReference.putFile(imageUri).addOnSuccessListener { _ ->
            fileReference.downloadUrl.addOnCompleteListener{taskSnapshot->
                val id = databaseReference.push().key
                val url = taskSnapshot.result.toString()
                toilet.imageURL = url
                id?.let { toilet.id = it }
                id?.let { databaseReference.child(it).setValue(toilet) }
                println("UPLOAD SUCCESS")
            }
        }.addOnFailureListener {
            println("UPLOAD FAILED")
        }
    }

}
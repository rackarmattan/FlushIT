package com.example.flushit.model

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * The data access object for users. As the users are authenticated through the Firebase authentication
 * system as well as created in the database, this class holds both a authentication variable
 * as well as a reference to the user database.
 */
class UserDAO {

    private var databaseReference = FirebaseDatabase.getInstance().getReference("users")
    //private var currentUserLiveData = FirebaseQueryLiveData(databaseReference)
    private var currentUserLiveData = FirebaseUserLiveData(databaseReference)
    private val auth = FirebaseAuth.getInstance()

    /**
     * Listen for changes in the state of the authentication. When a user has signed in, the
     * state will change and if there is a currentUser, the user is signed in. Set the current
     * user to the user with the same id as the newly signed in user.
     */
    init {
        auth.addAuthStateListener { auth ->
            auth.currentUser?.let {
                val uid = it.uid
                currentUserLiveData = FirebaseUserLiveData(databaseReference.child(uid))
            }
        }
    }

    /**
     * @return The current user as LiveData so no other class can change it.
     */
    fun getCurrentUser(): LiveData<User>{
        return currentUserLiveData
    }

    /**
     * Add a user in the user database.
     * @param user : The user to add
     */
    fun addUser(user:User){
        user.uid?.let { databaseReference.child(it).setValue(user) }
    }

    /**
     * Update a user in the database.
     * @param user : The user to update
     */
    fun updateUser(user:User){
        addUser(user)
    }

    fun deleteUser(user:User){
        user.uid?.let {
            databaseReference.child(it).removeValue()
        }
    }

}
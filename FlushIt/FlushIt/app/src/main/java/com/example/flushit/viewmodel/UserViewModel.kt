package com.example.flushit.viewmodel

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.flushit.errors.*
import com.example.flushit.model.*
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

/**
 * The viewmodel for users. This class handles communication between the views and the repository.
 * As this class is persistent when the activities/fragments are destroyed the data doesn't need
 * to be reloaded from the internet. Also handles sign up, log in and log out.
 * @param repository : The repository to get information from.
 */
class UserViewModel(private val repository: Repository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val permissionLevelUser = "user"
    private val permissionLevelAdmin = "admin"

    /**
     * Errors that are observed by the views. When the value changes, the observer
     * will be notified once.
     */
    private var error = SingleLiveEvent<CustomError>()

    /**
     * Get the current user from the repository.
     * @return The current user form the repository
     */
    fun getCurrentUser() = repository.getCurrentUser()

    /**
     * Tries to sign in a user through the Firebase Authorisation system. If the sign in
     * was successful, redirect the user to the main page.
     *
     * If the sign in wasn't successful, create a CustomError based on the Firebase excpetion.
     * Set the error variable's value to that error. As the error variable is a SingleLiveEvent,
     * the class that is observing the error will be notified by the change.
     *
     * @param context : The context where this function was called
     * @param redirectTo : The class to redirect to if the sign in was successful
     * @param email : The e-mail to sign in with
     * @param password : The password to sign in with
     */
    fun <T: AppCompatActivity>signInWithEmailAndPassword(context: Context, redirectTo: Class<T>, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) {
                val intent = Intent(context, redirectTo)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }
            else{
                println(it.exception)
                val customError: CustomError
                if(it.exception is FirebaseAuthInvalidUserException){
                    customError = NoCorrespondingUserError()
                }
                else if(it.exception is FirebaseAuthInvalidCredentialsException){
                    customError = InvalidPasswordError()
                }
                else if(it.exception is FirebaseNetworkException){
                    customError = NetworkError()
                }
                else{
                    customError = OtherError()
                }
                error.value = customError
            }
        }
    }

    /**
     * FireBase authorisation the function tries to create an account with the filled in email and
     * password. If this was successful, a user is created with the uid of the currentUser in
     * the Firebase Authorisation through the repository. After that, redirect to the requested
     * activity.
     *
     * If the registration wasn't successful, a CustomError is made based on the Firebase exception.
     * Set the error variable's value to that error. As the error variable is a SingleLiveEvent,
     * the class that is observing the error will be notified by the change.
     *
     * @param context : The context where this function was called
     * @param redirectTo : The class to redirect to if the sign up was successful
     * @param email : The e-mail to create an account with
     * @param password : The password to create an account with
     */
    fun <T: AppCompatActivity>createUserWithEmailAndPassword(context: Context, redirectTo: Class<T>, email:String, password: String){

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                val user = User()
                user.email = email
                user.permissionLevel = permissionLevelUser
                val id = auth.currentUser?.uid
                id?.let {
                    user.uid = it
                    repository.addUser(user)
                }
                val intent = Intent(context, redirectTo)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }
            else{
                println(it.exception)
                val customError: CustomError
                if(it.exception is FirebaseAuthUserCollisionException){
                    customError = DuplicateEmailError()
                }
                else if(it.exception is FirebaseNetworkException){
                    customError = NetworkError()
                }
                else{
                    customError = OtherError()
                }
                error.value = customError
            }
        }
    }

    /**
     * @return Error as LiveData som no other class can set its value
     */
    fun getError() = error as LiveData<CustomError>

    /**
     * Add a toilet to a user's list of rated toilet. Updates the user in the repository.
     * @param user : The user to add a toilet to
     * @param toilet : The toilet to add to the user
     */
    fun addToRatedToilets(user:User, toilet: Toilet){
        if(user.ratedToilets.contains(toilet)) return
        user.ratedToilets.add(toilet)
        repository.updateUser(user)
    }

    /**
     * @return true if a user is logged in, otherwise false.
     */
    fun isAUserLoggedIn(): Boolean {
        auth.currentUser?.let { return true }
        return false
    }

    /**
     * Sign out the user and redirect to a new activity.
     * @param context : The context where this function was called
     * @param redirectTo : The class to redirect to after log out
     */
    fun <T: AppCompatActivity>logOut(context:Context, redirectTo: Class<T>){
        auth.signOut()
        val intent = Intent(context, redirectTo)
        context.startActivity(intent)
    }
}
package com.example.flushit.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.utilities.InjectorUtils
import com.example.flushit.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_start_page.*

/**
 * The first page for the user if the user hasn't logged in before. Displays the "activity_start_page.xml".
 * This is where the UserViewModel is created.
 */

class StartPage : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel

    /**
     * First, the class is getting it's viewmodel. After that, it checks whether there is a user that
     * is currently logged in via the viewmodel. If a user is logged in, the user is directly navigated
     * to the MainNavigationHandler.
     *
     * Otherwise, all the view's click listeners are set.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeViewModel()


        if(viewModel.isAUserLoggedIn()) {
            val intent = Intent(this, MainNavigationHandler::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        setContentView(R.layout.activity_start_page)

        signUpText.setOnClickListener {
            openSignUpActivity()
        }


        loginButton.setOnClickListener { userLogin() }
    }

    /**
     * Get this class' viewmodel from the InjectorUtils.
     */
    private fun initializeViewModel(){
        val factory = InjectorUtils.provideUserViewModelFactor()
        viewModel = ViewModelProviders.of(this, factory).get(UserViewModel::class.java)
    }

    /**
     * This function tries to login a user. First, it checks whether the information
     * the user filled in is valid. If something is not valid, it's not possible to log in.
     * When something is wrong, an error appears where the information was wrong.
     *
     * If nothing is wrong, the viewmodel tries to log in the user.
     *
     * If the log in was successful, the user gets redirected to the main part of the app.
     * Otherwise, an error message is displayed by a toast message.
     */

    private fun userLogin() {
        val email = emailTextStart.text.toString().trim()
        val password = passwordTextStart.text.toString().trim()
        if(email.isEmpty()){
            emailTextStart.error = resources.getString(R.string.email_not_empty)
            emailTextStart.requestFocus()
            return
        }
        if(password.isEmpty()){
            passwordTextStart.error =  resources.getString(R.string.password_not_empty)
            passwordTextStart.requestFocus()
            return
        }

        if(password.length <= 5){
            passwordTextStart.error = resources.getString(R.string.password_longer_than)
            passwordTextStart.requestFocus()
            return
        }

        viewModel.signInWithEmailAndPassword(this, MainNavigationHandler::class.java, email, password)
        viewModel.getError().observe(this, Observer {
            Toast.makeText(this, it.displayError(), Toast.LENGTH_LONG).show()
        })

    }

    /**
     * Redirects the user to the sign up page.
     */
    private fun openSignUpActivity(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

}

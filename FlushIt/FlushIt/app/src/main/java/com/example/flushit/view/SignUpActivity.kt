package com.example.flushit.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.utilities.InjectorUtils
import com.example.flushit.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*

/**
 * Presenter class for the sign up view (activity_sign_up.xml). Creates a user via the viewmodel and signs in the user
 * that was created.
 */

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel

    /**
     * Initializes the class' viewmodel. Sets click listeners for all the view objects.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initializeViewModel()
        createAccountButton.setOnClickListener { registerUser() }
        goBackText.setOnClickListener { goBack() }
    }

    /**
     * Gets the viewmodel from the InjectorUtils.
     */
    private fun initializeViewModel(){
        val factory = InjectorUtils.provideUserViewModelFactor()
        viewModel = ViewModelProviders.of(this, factory).get(UserViewModel::class.java)
    }

    /**
     * Callback for the "Go to login page" text. Destroy this activity so the app is closed
     * when pressing back at the StartPage.kt
     */
    private fun goBack() {
        val intent = Intent(this, StartPage::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Destroy this activity when the back button is pressed so the app closes instead of
     * going back when the user is on the start page.
     */
    override fun onBackPressed() {
        goBack()
    }

    /**
     * This function tries to register a user. First, it checks whether the information
     * the user filled in is valid. If something is not valid, it's not possible to sign up.
     * When something is wrong, an error appears where the information was wrong.
     *
     * If nothing is wrong, the viewmodel tries to create an account with the given information.
     * If it's successful, the user gets redirected to the MainNavigationHandler.
     *
     * If something is wrong, errors are displayed by toasts. The errors are observed and comes
     * from the viewmodel. If no error occured, no toast is displayed.
     */

    private fun registerUser() {
        val email = emailText.text.toString().trim()
        val password = passwordText.text.toString().trim()
        val emailAgain = emailTextAgain.text.toString().trim()
        if (email.isEmpty()) {
            emailText.error = resources.getString(R.string.email_not_empty)
            emailText.requestFocus()
            return
        }
        if(!emailAgain.equals(email)){
            emailTextAgain.error = resources.getString(R.string.email_not_equal)
            emailTextAgain.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.error = resources.getString(R.string.not_email)
            emailText.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordText.error = resources.getString(R.string.password_not_empty)
            passwordText.requestFocus()
            return
        }

        if (password.length <= 5) {
            passwordText.error = resources.getString(R.string.password_longer_than)
            passwordText.requestFocus()
            return
        }

        viewModel.createUserWithEmailAndPassword(this, MainNavigationHandler::class.java, email, password)

        viewModel.getError().observe(this, Observer {
            Toast.makeText(this, it.displayError(), Toast.LENGTH_SHORT).show()
        })

    }
}

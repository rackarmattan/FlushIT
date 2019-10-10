package com.example.flushit.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.errors.EmailChanged
import com.example.flushit.model.User
import com.example.flushit.viewmodel.UserViewModel
import java.lang.IllegalStateException

class EditEmailDialog: AppCompatDialogFragment() {

    private lateinit var passwordTextView: TextView
    private lateinit var newEmailTextView: TextView
    private lateinit var newEmailTextViewAgain: TextView
    private lateinit var saveChangesButton: Button
    private lateinit var userViewModel: UserViewModel
    private lateinit var currentUser: User

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = it.layoutInflater.inflate(R.layout.edit_email_dialog, null)
            passwordTextView = view.findViewById(R.id.password_text_view)
            newEmailTextView = view.findViewById(R.id.new_email)
            newEmailTextViewAgain = view.findViewById(R.id.new_email_again)
            saveChangesButton = view.findViewById(R.id.save_changes)
            userViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java)
            userViewModel.getCurrentUser().observe(this, Observer {user ->
                currentUser = user
            })
            saveChangesButton.setOnClickListener { saveChanges() }
            builder.setView(view).setNegativeButton("Avbryt"){ _, _ ->     }
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }

    private fun saveChanges(){
        val newEmail = newEmailTextView.text.toString().trim()
        val password = passwordTextView.text.toString().trim()
        val emailAgain = newEmailTextViewAgain.text.toString().trim()

        if (newEmail.isEmpty()) {
            newEmailTextView.error = resources.getString(R.string.email_not_empty)
            newEmailTextView.requestFocus()
            return
        }
        if(!emailAgain.equals(newEmail)){
            newEmailTextViewAgain.error = resources.getString(R.string.email_not_equal)
            newEmailTextViewAgain.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            newEmailTextView.error = resources.getString(R.string.not_email)
            newEmailTextView.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordTextView.error = resources.getString(R.string.password_not_empty)
            passwordTextView.requestFocus()
            return
        }

        userViewModel.changeEmail(currentUser, newEmail, password)

        userViewModel.getError().observe(this, Observer {
            Toast.makeText(activity, it.displayError(), Toast.LENGTH_SHORT).show()
            if(it is EmailChanged) dismiss()
        })


    }

}
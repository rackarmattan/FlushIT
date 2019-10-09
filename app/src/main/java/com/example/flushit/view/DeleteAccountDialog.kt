package com.example.flushit.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.viewmodel.UserViewModel

class DeleteAccountDialog : AppCompatDialogFragment() {
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var userViewModel: UserViewModel
    private lateinit var deleteAccountButton: Button


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = it.layoutInflater.inflate(R.layout.delete_account_dialog, null)
            emailText = view.findViewById(R.id.email_text)
            passwordText = view.findViewById(R.id.password_text)
            deleteAccountButton = view.findViewById(R.id.delete_account)
            deleteAccountButton.setOnClickListener { deleteAccount() }
            userViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java)
            builder.setView(view).setNegativeButton("Avbryt"){ _, _ ->     }
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }

    private fun deleteAccount(){
        val email = emailText.text.toString().trim()
        val password = passwordText.text.toString().trim()
        if (email.isEmpty()) {
            emailText.error = resources.getString(R.string.email_not_empty)
            emailText.requestFocus()
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

        userViewModel.deleteUserAccount(email, password)
    }


}
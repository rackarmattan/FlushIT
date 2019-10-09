package com.example.flushit.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.viewmodel.UserViewModel

class SettingsFragment: Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var deleteAccountButton: Button
    private lateinit var editEmailButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        activity?.let { userViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java) }

        deleteAccountButton = view.findViewById(R.id.delete_account_button)
        deleteAccountButton.setOnClickListener { deleteAccount() }

        editEmailButton = view.findViewById(R.id.edit_email_button)
        editEmailButton.setOnClickListener { editEmail() }


        return view
    }


    private fun deleteAccount(){
        //TODO delete account, first alert dialog then actually delete account, after that redirecting to Login page
        //TODO use this link, re-ask for email and password in an alert dialog https://firebase.google.com/docs/auth/android/manage-users#re-authenticate_a_user
        val dialog = DeleteAccountDialog()
        fragmentManager?.let { dialog.show(it, "DeleteDialog") }
    }

    private fun editEmail(){

    }
}
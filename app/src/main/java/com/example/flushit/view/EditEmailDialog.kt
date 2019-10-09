package com.example.flushit.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.flushit.R
import java.lang.IllegalStateException

class EditEmailDialog: AppCompatDialogFragment() {

    private lateinit var oldEmailTextView: TextView
    private lateinit var newEmailTextView: TextView
    private lateinit var newEmailTextViewAgain: TextView
    private lateinit var saveChangesButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = it.layoutInflater.inflate(R.layout.edit_email_dialog, null)
            oldEmailTextView = view.findViewById(R.id.old_email)
            newEmailTextView = view.findViewById(R.id.new_email)
            newEmailTextViewAgain = view.findViewById(R.id.new_email_again)
            saveChangesButton = view.findViewById(R.id.save_changes)
            saveChangesButton.setOnClickListener { saveChanges() }
            builder.setView(view).setNegativeButton("Avbryt"){ _, _ ->     }
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }

    private fun saveChanges(){

    }

}
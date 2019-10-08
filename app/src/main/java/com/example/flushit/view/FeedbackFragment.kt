package com.example.flushit.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flushit.R

class FeedbackFragment : Fragment() {
    private lateinit var emailMe: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_feedback, container, false)
        emailMe = view.findViewById(R.id.email_me_text)
        emailMe.setOnClickListener { openEmail() }
        (activity as? AppCompatActivity)?.let { it.supportActionBar?.title = resources.getString(R.string.feedback) }
        return view
    }

    private fun openEmail(){
        val email = resources.getString(R.string.email_address)
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",email, null))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        startActivity(Intent.createChooser(intent, "Send email..."))
    }

}
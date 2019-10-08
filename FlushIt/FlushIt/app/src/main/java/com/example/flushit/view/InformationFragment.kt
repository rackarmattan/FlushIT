package com.example.flushit.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.flushit.R

/**
 * Fragment that displays the information about the app.
 */
class InformationFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.let { it.supportActionBar?.title = resources.getString(R.string.about) }
        return inflater.inflate(R.layout.fragment_information, container, false)
    }


}

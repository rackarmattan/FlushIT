package com.example.flushit.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flushit.R
import com.google.android.material.navigation.NavigationView

//TODO testa sätt denna för favourites och maps och kom ihåg att sätta all R.....id:s

abstract class AbstractSideOptionFragment : Fragment() {
    protected abstract val fragmentActionBarText: Int
    protected abstract val fragmentNavigationID: Int
    protected abstract val layoutID: Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val nav = activity?.findViewById<NavigationView>(R.id.nav_view)
        val menu = nav?.menu
        val menuItem = menu?.findItem(fragmentNavigationID)
        menuItem?.let {
            if (!it.isChecked) it.isChecked = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.title = resources.getString(fragmentActionBarText)
        }

        return inflater.inflate(layoutID, container, false)

    }

}
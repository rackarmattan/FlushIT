package com.example.flushit.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.flushit.R
import com.example.flushit.model.*
import com.example.flushit.utilities.InjectorUtils
import com.example.flushit.viewmodel.UserViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.navigation_main_activity.*


/**
 * This is the activity that holds the app's fragments. It handles the navigation drawer (menu),
 * navigates between the fragments and can log out the user.
 */
class MainNavigationHandler : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userViewModel: UserViewModel

    /**
     * Starts with getting the activity's viewmodel, which handle user related actions and
     * keeps track of the current user. After that, the navigation view (side menu) and tool bar (top
     * action bar) is set.
     *
     * If it's the first time the activity is created, the fragment with the map is instantiated
     * and set as the current view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_main_activity)

        initializeUserViewModel()

        val toolBar: androidx.appcompat.widget.Toolbar = toolbar
        setSupportActionBar(toolBar)

        drawerLayout = drawer_layout
        val navigationView: NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)

        logOutButton.setOnClickListener { signOut() }

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.bringToFront()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MapsFragment()
            )
                .addToBackStack(null)
                .commit()
            navigationView.setCheckedItem(R.id.nav_map)
        }
    }

    /**
     * Get this view's viewmodel from the InjectorUtils. After that it observes the current user from
     * the viewmodel and sets the logged in text to the current user's e-mail.
     */
    private fun initializeUserViewModel() {
        val factory = InjectorUtils.provideUserViewModelFactor()
        userViewModel = ViewModelProviders.of(this, factory).get(UserViewModel::class.java)
        userViewModel.getCurrentUser().observe(this, Observer {
            val user = it.getValue(User::class.java)
            inloggedAsText.text = resources.getString(R.string.logged_in_as, user?.email)
        })
    }

    /**
     * If the side menu is open when the back button is pressed, the side menu is closed.
     *
     * Otherwise, check if this is the first time the back button is pressed. If so,
     * the activity is destroyed and thw whole app is closed. If it's not the first time,
     * the most recent fragment is displayed again.
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val count = supportFragmentManager.backStackEntryCount
            if (count < 2){
                finish()
            }else {
                supportFragmentManager.popBackStack()
            }
        }
    }

    /**
     * Function for navigating between the items in the side menu. If an item is pressed,
     * the corresponding fragment is created and navigated to.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_map -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MapsFragment()
            ).addToBackStack(null).commit()
            R.id.nav_favorites -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                FavouritesFragment()
            ).addToBackStack(null).commit()
            R.id.nav_about -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                InformationFragment()
            ).addToBackStack(null).commit()
            R.id.nav_feedback -> println("Feedback pressed")
            R.id.nav_share -> println("Shared pressed")
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true;
    }

    /**
     * Click listener for the log out button in the side menu. If it's pressed,
     * the viewmodel's sign out method is called. After that, destroy this activity to
     * make it impossible for the user to go back.
     */
    private fun signOut() {
        userViewModel.logOut(this, StartPage::class.java)
        finish()
    }


}
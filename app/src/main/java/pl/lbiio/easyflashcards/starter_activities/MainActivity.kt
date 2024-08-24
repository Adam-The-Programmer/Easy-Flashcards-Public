package pl.lbiio.easyflashcards.starter_activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.drawer_items.buying.BuyActivity
import pl.lbiio.easyflashcards.databinding.ActivityMainBinding
import pl.lbiio.easyflashcards.drawer_items.*
import pl.lbiio.easyflashcards.foreground_services.BackupForegroundService

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private var navigationView: NavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.mainActivityToolbar.root
        setSupportActionBar(toolbar)

        drawerLayout = binding.myDrawerLayout
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        navigationView = binding.navigationView

        navigationView?.setNavigationItemSelectedListener(this)
        val fragment: Fragment = MainPackages()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .add(R.id.content_frame, fragment)
            .commit()
        ContextCompat.startForegroundService(this, Intent(this, BackupForegroundService::class.java))
    }

    override fun onStart() {
        super.onStart()
        navigationView?.setCheckedItem(R.id.packages_item)
        //replaceFragment(MainPackages())

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.packages_item -> {
                replaceFragment(MainPackages())
            }
            R.id.buy_item -> {
                navigationView?.setCheckedItem(R.id.packages_item)
                    val intent = Intent(this, BuyActivity::class.java)
                    startActivity(intent)
            }

            R.id.leaderboard_item -> replaceFragment(LeaderBoardFragment())

            R.id.awards_item -> replaceFragment(AwardsFragment())

        }

        val drawer = findViewById<View>(R.id.my_drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.my_drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            navigationView?.setCheckedItem(R.id.packages_item)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .replace(R.id.content_frame, fragment)
            .addToBackStack(null)
            .commit()
    }

}


package com.example.papertraderv2

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.papertraderv2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)
        binding.sideNavView.setupWithNavController(navController)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.topAppBar.title = when (destination.id) {
                R.id.nav_home -> "Home"
                R.id.tradeFragment -> "Trade"
                R.id.nav_learn -> "Learn"
                R.id.nav_forum -> "Forum"
                R.id.moduleDetailFragment -> "Module"
                R.id.lessonDetailFragment -> "Lesson"
                R.id.nav_settings -> "Profile"
                else -> destination.label?.toString().orEmpty()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        showFirstTimeTutorialIfNeeded()
    }

    private fun showFirstTimeTutorialIfNeeded() {
        val prefs = getSharedPreferences("papertrader_prefs", MODE_PRIVATE)
        val hasSeenTutorial = prefs.getBoolean("has_seen_tutorial", false)

        if (!hasSeenTutorial) {
            AlertDialog.Builder(this)
                .setTitle("Welcome to PaperTrader")
                .setMessage(
                    "Use the bottom navigation bar to move through the app.\n\n" +
                            "• Trade: Search stocks, view prices, and place simulated buy/sell trades.\n" +
                            "• Home: Review your current trades, watchlist, and past trades.\n" +
                            "• Learn: Complete trading lessons.\n" +
                            "• Forum: Create posts, comment, and learn from other users.\n\n" +
                            "All trades use virtual money, so you can practice without financial risk."
                )
                .setPositiveButton("Start Practicing") { dialog, _ ->
                    prefs.edit().putBoolean("has_seen_tutorial", true).apply()
                    dialog.dismiss()
                }
                .setNegativeButton("Show Again Later") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                navController.navigate(R.id.nav_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(binding.drawerLayout) || super.onSupportNavigateUp()
    }
}
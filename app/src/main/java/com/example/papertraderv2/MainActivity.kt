package com.example.papertraderv2

import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.papertraderv2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)
        binding.sideNavView.setupWithNavController(navController)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

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

        showFirstTimeTutorialIfNeeded()
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

    private fun showFirstTimeTutorialIfNeeded() {
        val uid = auth.currentUser?.uid ?: "guest"
        val prefs = getSharedPreferences("papertrader_prefs", MODE_PRIVATE)
        val key = "has_seen_tutorial_$uid"
        val hasSeenTutorial = prefs.getBoolean(key, false)

        if (!hasSeenTutorial) {
            AlertDialog.Builder(this)
                .setTitle("Welcome to PaperTrader")
                .setMessage(
                    "PaperTrader helps you practice trading without using real money.\n\n" +
                            "• Home: View your portfolio, current trades, watchlist, and past trades.\n" +
                            "• Trade: Search stocks, view prices, and place simulated buy/sell trades.\n" +
                            "• Learn: Complete trading lessons.\n" +
                            "• Forum: Create posts, comment, and learn from others.\n\n" +
                            "All trades are simulated for education and practice."
                )
                .setPositiveButton("Start Practicing") { dialog, _ ->
                    prefs.edit().putBoolean(key, true).apply()
                    dialog.dismiss()
                }
                .setNegativeButton("Show Again Later") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
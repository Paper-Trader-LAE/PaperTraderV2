package com.example.papertraderv2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Enable Firebase Crashlytics (safe to keep enabled)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)

// Bottom nav actions
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.nav_portfolio -> {
                    startActivity(Intent(this, PortfolioActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

// Top bar actions (menu click)
        topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    true
                }
                else -> false
            }
        }

        /*
        //TEST CRASH CODE — COMMENTED OUT
        val crashButton = findViewById<Button>(R.id.testCrashButton)
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash from XML button!")
        }
        */
    }
}
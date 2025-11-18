package com.example.papertraderv2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply safe area insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Enable Firebase Crashlytics (safe to keep enabled)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        /*
        //TEST CRASH CODE — COMMENTED OUT
        val crashButton = findViewById<Button>(R.id.testCrashButton)
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash from XML button!")
        }
        */
    }
}
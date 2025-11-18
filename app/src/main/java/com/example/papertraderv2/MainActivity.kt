package com.example.papertraderv2

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // -----------------------------------------------------------
        // Crashlytics Test Button (Kotlin version)
        // -----------------------------------------------------------

        val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setBackgroundColor(Color.RED)
        crashButton.setTextColor(Color.WHITE)

        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash")  // Force a crash
        }

        addContentView(
            crashButton,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }
}
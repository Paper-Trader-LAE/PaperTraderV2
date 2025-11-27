package com.example.papertraderv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val email = findViewById<EditText>(R.id.loginEmail)
        val password = findViewById<EditText>(R.id.loginPassword)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val toSignup = findViewById<TextView>(R.id.toSignup)

        // 🔥 TEMP: BYPASS FIREBASE AND GO STRAIGHT TO MAIN
        loginBtn.setOnClickListener {
            if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
                Toast.makeText(this, "Fill in all fields first", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        toSignup.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }
}
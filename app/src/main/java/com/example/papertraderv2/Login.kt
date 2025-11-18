
package com.example.papertraderv2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login: AppCompatActivity() {

    // Firebase Auth object handles sign-in / sign-out
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()

        // Link XML elements to Kotlin
        val email = findViewById<EditText>(R.id.loginEmail)
        val password = findViewById<EditText>(R.id.loginPassword)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val toSignup = findViewById<TextView>(R.id.toSignup)

        // When user presses “Login”
        loginBtn.setOnClickListener {
            val userEmail = email.text.toString().trim()
            val userPass = password.text.toString().trim()

            // Validate that fields are not empty
            if (userEmail.isNotEmpty() && userPass.isNotEmpty()) {

                // Try signing in with Firebase
                auth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                        // Navigate to main/home screen (you’ll create this next)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish() // close login screen so user can’t go back
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // “Don’t have an account? Sign Up” → go to sign-up page
        toSignup.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }
}

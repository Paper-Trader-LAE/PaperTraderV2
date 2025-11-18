package com.example.papertraderv2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val email = findViewById<EditText>(R.id.signupEmail)
        val password = findViewById<EditText>(R.id.signupPassword)
        val signupBtn = findViewById<Button>(R.id.signupBtn)
        val toLogin = findViewById<TextView>(R.id.toLogin)

        signupBtn.setOnClickListener {
            val userEmail = email.text.toString().trim()
            val userPass = password.text.toString().trim()

            if (userEmail.isEmpty() || userPass.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnSuccessListener { result ->
                    val userId = result.user?.uid ?: return@addOnSuccessListener

                    val userData = hashMapOf(
                        "email" to userEmail,
                        "balance" to 20000.0,
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    db.collection("users").document(userId).set(userData)

                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Sign up failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        toLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }
}
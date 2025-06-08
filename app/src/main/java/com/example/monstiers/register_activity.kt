package com.example.monstiers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class register_activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerEmail = findViewById<EditText>(R.id.editTextRegisterEmail)
        val registerPassword = findViewById<EditText>(R.id.editTextRegisterPassword)
        val confirmPassword = findViewById<EditText>(R.id.editTextRegisterConfirmPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        buttonRegister.setOnClickListener {
            val email = registerEmail.text.toString().trim()
            val password = registerPassword.text.toString().trim()
            val confirm = confirmPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val userId = result.user?.uid
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "email" to email,
                            "money" to 0,
                            "level" to 0,
                            "packsOpened" to 0,
                            "cardsOwned" to 0,
                            "ownedCards" to listOf<Int>()
                        )

                        firestore.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, set_profile_activity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user to Firestore", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

        }
    }
}

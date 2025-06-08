package com.example.monstiers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class set_profile_activity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val setProfPic = findViewById<ImageView>(R.id.imageViewProfPicSet)
        val setNick = findViewById<EditText>(R.id.editTextSetNick)
        val setName = findViewById<EditText>(R.id.editTextSetName)
        val setSurname = findViewById<EditText>(R.id.editTextSetSurname)
        val saveButton = findViewById<Button>(R.id.buttonSetSave)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        saveButton.setOnClickListener {
            val nick = setNick.text.toString().trim()
            val name = setName.text.toString().trim()
            val surname = setSurname.text.toString().trim()

            if (nick.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                val profileData = mapOf(
                    "nick" to nick,
                    "name" to name,
                    "surname" to surname
                )

                firestore.collection("users").document(userId)
                    .set(profileData, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, mainpage_activity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

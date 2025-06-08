package com.example.monstiers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class write_mail_activity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_write_mail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logoPic = findViewById<ImageView>(R.id.imageViewLogo)
        val hamburger = findViewById<ImageView>(R.id.imageViewHamburger)
        val profilePic = findViewById<ImageView>(R.id.imageViewProfilePicButton)
        val inboxImage = findViewById<ImageView>(R.id.imageViewInboxButton)
        val homeImage = findViewById<ImageView>(R.id.imageViewHomeButton)
        val storeImage = findViewById<ImageView>(R.id.imageViewStoreButton)
        val moneyText = findViewById<TextView>(R.id.textViewPlayerMoney)
        val mailText = findViewById<EditText>(R.id.editTextMail)
        val mailNick = findViewById<EditText>(R.id.editTextMailNick)
        val sendButton = findViewById<Button>(R.id.buttonMailSend)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val userRef = firestore.collection("users").document(userId!!)

        sendButton.setOnClickListener {
            val recipientNick = mailNick.text.toString().trim()
            val message = mailText.text.toString().trim()

            if (recipientNick.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { senderDoc ->
                    val senderNickname = senderDoc.getString("nick") ?: "Unknown"

                    firestore.collection("users")
                        .whereEqualTo("nick", recipientNick)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val mail = hashMapOf(
                                    "to" to recipientNick,
                                    "from" to senderNickname,
                                    "message" to message,
                                    "timestamp" to System.currentTimeMillis()
                                )

                                firestore.collection("mails")
                                    .add(mail)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Mail sent!", Toast.LENGTH_SHORT).show()
                                        mailText.setText("")
                                        mailNick.setText("")
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to send mail", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "User with nickname '$recipientNick' not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error finding recipient: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
        }



        fun updateMoneyText(money: Long) {
            moneyText.text = "$: %04d".format(money)
        }

        userRef.get().addOnSuccessListener { doc ->
            val money = doc.getLong("money") ?: 0L
            updateMoneyText(money)
        }

        logoPic.setOnClickListener {
            userRef.get().addOnSuccessListener { doc ->
                val money = doc.getLong("money") ?: 0L
                val newMoney = money + 100
                userRef.update("money", newMoney).addOnSuccessListener {
                    updateMoneyText(newMoney)
                }
            }
        }

        homeImage.setOnClickListener {
            val intent = Intent(this, mainpage_activity::class.java)
            startActivity(intent)
        }

        storeImage.setOnClickListener {
            val intent = Intent(this, pack_select_activity::class.java)
            startActivity(intent)
        }

        profilePic.setOnClickListener {
            val intent = Intent(this, ProfileView_activity::class.java)
            startActivity(intent)
        }

        inboxImage.setOnClickListener {
            val intent = Intent(this, Inbox_activity::class.java)
            startActivity(intent)
        }



    }
}
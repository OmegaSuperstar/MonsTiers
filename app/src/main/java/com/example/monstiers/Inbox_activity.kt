package com.example.monstiers

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Inbox_activity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inbox)
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
        val writeMail = findViewById<TextView>(R.id.textViewWriteMail)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val userRef = firestore.collection("users").document(userId!!)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMails)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (currentUser != null) {
            val nickname = currentUser.displayName
            firestore.collection("mails")
                .whereEqualTo("to", nickname)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val mailList = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Mail::class.java)
                    }
                    recyclerView.adapter = MailAdapter(mailList)
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

        writeMail.setOnClickListener {
            val intent = Intent(this, write_mail_activity::class.java)
            startActivity(intent)
        }
    }
}
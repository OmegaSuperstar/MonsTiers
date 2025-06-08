package com.example.monstiers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class ProfileView_activity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nickname = findViewById<TextView>(R.id.textViewNickname)
        val nameAndSurname = findViewById<TextView>(R.id.textViewNameAndSurname)
        val level = findViewById<TextView>(R.id.textViewLevel)
        val packsOpened = findViewById<TextView>(R.id.textViewPacksOpened)
        val cardsInCollection = findViewById<TextView>(R.id.textViewCardsInCollection)
        val buttonViewPlayerCol = findViewById<Button>(R.id.buttonViewPlayerCollection)
        val buttonViewPlayerAch = findViewById<Button>(R.id.buttonViewPlayerAchievements)
        val buttonReviewPlayerCard = findViewById<Button>(R.id.buttonReviewPlayerCard)
        val buttonShareCard = findViewById<Button>(R.id.buttonShareCard)
        val inboxImage = findViewById<ImageView>(R.id.imageViewInboxButton)
        val homeImage = findViewById<ImageView>(R.id.imageViewHomeButton)
        val storeImage = findViewById<ImageView>(R.id.imageViewStoreButton)
        val logOut = findViewById<TextView>(R.id.textViewLogOut)


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    nickname.text = doc.getString("nick") ?: "Nick"
                    val name = doc.getString("name") ?: ""
                    val surname = doc.getString("surname") ?: ""
                    nameAndSurname.text = "$name $surname"
                    level.text = "Level: ${doc.getLong("level") ?: 1}"
                    packsOpened.text = "Packs opened: ${doc.getLong("packsOpened") ?: 0}"
                    cardsInCollection.text = "Cards in collection: ${doc.getLong("cardsOwned") ?: 0}"
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

        inboxImage.setOnClickListener {
            val intent = Intent(this, Inbox_activity::class.java)
            startActivity(intent)
        }

        buttonViewPlayerCol.setOnClickListener {
            val intent = Intent(this, card_collection_activity::class.java)
            startActivity(intent)
        }

        buttonShareCard.setOnClickListener {
            val intent = Intent(this, share_card_activity::class.java)
            startActivity(intent)
        }

        logOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
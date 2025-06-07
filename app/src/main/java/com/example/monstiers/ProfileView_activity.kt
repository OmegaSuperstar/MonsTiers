package com.example.monstiers

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class ProfileView_activity : AppCompatActivity() {
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
        val inboxImage = findViewById<ImageView>(R.id.imageViewInboxButton)
        val homeImage = findViewById<ImageView>(R.id.imageViewHomeButton)
        val storeImage = findViewById<ImageView>(R.id.imageViewStoreButton)
    }
}
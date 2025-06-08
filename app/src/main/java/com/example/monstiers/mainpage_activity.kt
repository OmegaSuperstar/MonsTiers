package com.example.monstiers

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.os.Build
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


class mainpage_activity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mainpage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val logoPic = findViewById<ImageView>(R.id.imageViewLogo)
        val hamburger = findViewById<ImageView>(R.id.imageViewHamburger)
        val profilePic = findViewById<ImageView>(R.id.imageViewProfilePicButton)
        val moneyText = findViewById<TextView>(R.id.textViewPlayerMoney)
        val buttonOpenPacks = findViewById<Button>(R.id.buttonCollection)
        val buttonBuyPacks = findViewById<Button>(R.id.buttonBuyPacks)
        val buttonCollection = findViewById<Button>(R.id.buttonCollection)
        val featured1 = findViewById<TextView>(R.id.textViewFeatured1)
        val featured2 = findViewById<TextView>(R.id.textViewFeatured2)
        val featured3 = findViewById<TextView>(R.id.textViewFeatured3)
        val inboxImage = findViewById<ImageView>(R.id.imageViewInboxButton)
        val homeImage = findViewById<ImageView>(R.id.imageViewHomeButton)
        val storeImage = findViewById<ImageView>(R.id.imageViewStoreButton)
        val featuredPrice1 = findViewById<TextView>(R.id.textViewFeaturedPrice1)
        val featuredPrice2 = findViewById<TextView>(R.id.textViewFeaturedPrice2)
        val featuredPrice3 = findViewById<TextView>(R.id.textViewFeaturedPrice3)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        fun updateMoneyDisplay(money: Long) {
            moneyText.text = "$: %04d".format(money)
        }

        fun updateFeaturedPacks() {
            firestore.collection("packs")
                .orderBy("numbersOpened", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val packs = querySnapshot.documents

                    if (packs.size > 0) {
                        featured1.text = packs[0].getString("packName") ?: "N/A"
                        val price1 = packs[0].getDouble("price") ?: 0.0
                        featuredPrice1.text = "$%.2f".format(price1)
                    } else {
                        featured1.text = "N/A"
                        featuredPrice1.text = ""
                    }

                    if (packs.size > 1) {
                        featured2.text = packs[1].getString("packName") ?: "N/A"
                        val price2 = packs[1].getDouble("price") ?: 0.0
                        featuredPrice2.text = "$%.2f".format(price2)
                    } else {
                        featured2.text = "N/A"
                        featuredPrice2.text = ""
                    }

                    if (packs.size > 2) {
                        featured3.text = packs[2].getString("packName") ?: "N/A"
                        val price3 = packs[2].getDouble("price") ?: 0.0
                        featuredPrice3.text = "$%.2f".format(price3)
                    } else {
                        featured3.text = "N/A"
                        featuredPrice3.text = ""
                    }
                }
                .addOnFailureListener {
                    featured1.text = "Error loading packs"
                    featuredPrice1.text = ""
                    featured2.text = ""
                    featuredPrice2.text = ""
                    featured3.text = ""
                    featuredPrice3.text = ""
                }
        }



        val currentUser = auth.currentUser
        val userRef = firestore.collection("users").document(currentUser!!.uid)

        userRef.get().addOnSuccessListener { doc ->
            val money = doc.getLong("money") ?: 0L
            updateMoneyDisplay(money)
        }

        logoPic.setOnClickListener {
            userRef.get().addOnSuccessListener { doc ->
                val money = doc.getLong("money") ?: 0L
                val newMoney = money + 100
                userRef.update("money", newMoney)
                updateMoneyDisplay(newMoney)
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

        profilePic.setOnClickListener {
            val intent = Intent(this, ProfileView_activity::class.java)
            startActivity(intent)
        }

        buttonBuyPacks.setOnClickListener {
            val intent = Intent(this, pack_select_activity::class.java)
            startActivity(intent)
        }

        buttonCollection.setOnClickListener {
            val intent = Intent(this, card_collection_activity::class.java)
            startActivity(intent)
        }

        updateFeaturedPacks()
    }
}
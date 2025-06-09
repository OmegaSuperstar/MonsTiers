package com.example.monstiers

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class pack_open_activity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pack_open)
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
        val packOpen = findViewById<ImageView>(R.id.imageViewPackOpen)
        val packName = findViewById<TextView>(R.id.textViewPackOpenName)
        val packId = intent.getStringExtra("packId") ?: return
        val packNameStr = intent.getStringExtra("packName") ?: "Unknown Pack"
        val packPrice = intent.getLongExtra("packPrice", 0L)
        val cardIds = intent.getStringArrayListExtra("packCardIds") ?: arrayListOf()

        packName.text = packNameStr


        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val userRef = firestore.collection("users").document(userId!!)

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

        packOpen.setOnClickListener {
            if (cardIds.size < 2) {
                Toast.makeText(this, "Not enough cards in pack", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCardIds = cardIds.shuffled().take(2)

            firestore.collection("cards")
                .whereIn(FieldPath.documentId(), selectedCardIds)
                .get()
                .addOnSuccessListener { result ->
                    val cardNames = result.map { it.getString("cardName") ?: "Unknown" }

                    userRef.get().addOnSuccessListener { doc ->
                        val money = doc.getLong("money") ?: 0L
                        if (money < packPrice) {
                            Toast.makeText(this, "Not enough money!", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        val ownedCards = (doc.get("ownedCards") as? List<String>)?.toMutableList() ?: mutableListOf()
                        val packsOpened = doc.getLong("packsOpened") ?: 0L
                        val cardsOwned = doc.getLong("cardsOwned") ?: 0L

                        ownedCards.addAll(selectedCardIds)
                        val newCardsOwnedCount = cardsOwned + selectedCardIds.size
                        val newLevel = (newCardsOwnedCount / 5).toInt()

                        userRef.update(
                            mapOf(
                                "money" to (money - packPrice),
                                "ownedCards" to ownedCards,
                                "packsOpened" to packsOpened + 1,
                                "cardsOwned" to newCardsOwnedCount,
                                "level" to newLevel
                            )
                        ).addOnSuccessListener {
                            updateMoneyText(money - packPrice)
                            Toast.makeText(this, "You got: ${cardNames.joinToString(", ")}", Toast.LENGTH_LONG).show()

                            firestore.collection("packs").document(packId)
                                .update("numbersOpened", FieldValue.increment(1))

                            packOpen.postDelayed({
                                val intent = Intent(this, pack_select_activity::class.java)
                                startActivity(intent)
                                finish()
                            }, 2000)

                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show()
                        }

                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load card names", Toast.LENGTH_SHORT).show()
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
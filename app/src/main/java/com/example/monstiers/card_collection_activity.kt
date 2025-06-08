package com.example.monstiers

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class card_collection_activity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card_collection)

        recyclerView = findViewById(R.id.recyclerViewCollection)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        adapter = CardAdapter(emptyList())
        recyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val currentUser = auth.currentUser
        val userRef = currentUser?.let { firestore.collection("users").document(it.uid) }

        if (currentUser != null) {
            userRef?.get()?.addOnSuccessListener { userDoc ->
                val ownedCardNums = (userDoc["ownedCards"] as? List<*>)
                    ?.mapNotNull {
                        when (it) {
                            is Number -> it.toInt()
                            is String -> it.toIntOrNull()
                            else -> null
                        }
                    } ?: listOf()
                Log.d("CardCollection", "Owned Cards: $ownedCardNums")
                if (ownedCardNums.isEmpty()) {
                    Log.d("CardCollection", "No cards owned.")
                    return@addOnSuccessListener
                }
                firestore.collection("cards")
                    .whereIn("cardNum", ownedCardNums)
                    .get()
                    .addOnSuccessListener { cardDocs ->
                        val cardList = cardDocs.mapNotNull { doc ->
                            val cardNum = doc.getLong("cardNum") ?: doc.getString("cardNum")?.toIntOrNull()
                            val cardName = doc.getString("cardName")
                            val cardTier = doc.getLong("cardTier") ?: doc.getString("cardTier")?.toIntOrNull()
                            val cardType1 = doc.getString("cardType1") ?: "None"
                            val cardType2 = doc.getString("cardType2") ?: "None"
                            val cardSuper = doc.getBoolean("cardSuper")
                            if (cardNum != null && cardName != null && cardTier != null) {
                                Card(
                                    cardNum = cardNum,
                                    cardName = cardName,
                                    cardTier = cardTier,
                                    cardType1 = cardType1,
                                    cardType2 = cardType2,
                                    cardSuper = cardSuper ?: false
                                ).also {
                                    Log.d("CardCollection", "Loaded: ${it.cardName}")
                                }
                            } else {
                                Log.e("CardCollection", "Card data is incomplete for document: ${doc.id}")
                                null
                            }
                        }
                        adapter.cards = cardList
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Log.e("CardCollection", "Failed to load cards: ${it.message}")
                    }
            }?.addOnFailureListener {
                Log.e("CardCollection", "Failed to retrieve user data: ${it.message}")
            }
        }

        val logoPic = findViewById<ImageView>(R.id.imageViewLogo)
        val hamburger = findViewById<ImageView>(R.id.imageViewHamburger)
        val profilePic = findViewById<ImageView>(R.id.imageViewProfilePicButton)
        val inboxImage = findViewById<ImageView>(R.id.imageViewInboxButton)
        val homeImage = findViewById<ImageView>(R.id.imageViewHomeButton)
        val storeImage = findViewById<ImageView>(R.id.imageViewStoreButton)
        val moneyText = findViewById<TextView>(R.id.textViewPlayerMoney)

        fun updateMoneyText(money: Long) {
            moneyText.text = "$: %04d".format(money)
        }

        userRef?.get()?.addOnSuccessListener { doc ->
            val money = doc.getLong("money") ?: 0L
            updateMoneyText(money)
        }

        logoPic.setOnClickListener {
            userRef?.get()?.addOnSuccessListener { doc ->
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

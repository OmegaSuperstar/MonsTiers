package com.example.monstiers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class pack_select_activity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var packList = mutableListOf<Pack>()
    private var currentPackIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pack_select)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val packNameText = findViewById<TextView>(R.id.textViewPackName)
        val packCardCountText = findViewById<TextView>(R.id.textViewCardCount)
        val packCostText = findViewById<TextView>(R.id.textViewPackCost)

        val arrowLeft = findViewById<ImageView>(R.id.imageViewArrowLeft)
        val arrowRight = findViewById<ImageView>(R.id.imageViewArrowRight)

        val moneyText = findViewById<TextView>(R.id.textViewPlayerMoney)
        val logoPic = findViewById<ImageView>(R.id.imageViewLogo)
        val profilePic = findViewById<ImageView>(R.id.imageViewProfilePicButton)
        val homeImage = findViewById<ImageView>(R.id.imageViewHomeButton)
        val storeImage = findViewById<ImageView>(R.id.imageViewStoreButton)
        val inboxImage = findViewById<ImageView>(R.id.imageViewInboxButton)
        val openButton = findViewById<Button>(R.id.buttonOpenPack)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val userRef = firestore.collection("users").document(userId!!)

        fun updateMoneyText(money: Long) {
            moneyText.text = "$: %04d".format(money)
        }

        fun updatePackDisplay(pack: Pack) {
            packNameText.text = pack.packName
            packCardCountText.text = "Cards: ${pack.cardCount}"
            packCostText.text = "Cost: ${pack.price}"
        }

        firestore.collection("packs").get()
            .addOnSuccessListener { result ->
                packList.clear()
                for (doc in result) {
                    val pack = Pack(
                        id = doc.id,
                        packName = doc.getString("packName") ?: "Unnamed Pack",
                        cardCount = (doc.getLong("cardCount") ?: 0L).toInt(),
                        price = doc.getLong("price") ?: 0L,
                        cardIds = doc.get("cardIds") as? List<String> ?: emptyList()
                    )

                    packList.add(pack)
                }

                if (packList.isNotEmpty()) {
                    updatePackDisplay(packList[currentPackIndex])
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load packs", Toast.LENGTH_SHORT).show()
            }

        arrowLeft.setOnClickListener {
            if (packList.isNotEmpty()) {
                currentPackIndex = (currentPackIndex - 1 + packList.size) % packList.size
                updatePackDisplay(packList[currentPackIndex])
            }
        }

        arrowRight.setOnClickListener {
            if (packList.isNotEmpty()) {
                currentPackIndex = (currentPackIndex + 1) % packList.size
                updatePackDisplay(packList[currentPackIndex])
            }
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

        openButton.setOnClickListener {
            val selectedPack = packList.getOrNull(currentPackIndex)

            if (selectedPack == null || selectedPack.cardIds.size < 2) {
                Toast.makeText(this, "Invalid pack or not enough cards!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, pack_open_activity::class.java)
            intent.putExtra("packId", selectedPack.id)
            intent.putExtra("packName", selectedPack.packName)
            intent.putExtra("packPrice", selectedPack.price)
            intent.putStringArrayListExtra("packCardIds", ArrayList(selectedPack.cardIds))
            startActivity(intent)
        }

        homeImage.setOnClickListener {
            startActivity(Intent(this, mainpage_activity::class.java))
        }
        storeImage.setOnClickListener {
            startActivity(Intent(this, pack_select_activity::class.java))
        }
        inboxImage.setOnClickListener {
            startActivity(Intent(this, Inbox_activity::class.java))
        }
        profilePic.setOnClickListener {
            startActivity(Intent(this, ProfileView_activity::class.java))
        }
    }
}

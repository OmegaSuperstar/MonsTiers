package com.example.monstiers

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
import org.w3c.dom.Text

class mainpage_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mainpage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val hamburger = findViewById<ImageView>(R.id.imageViewHamburger)
        val profilePic = findViewById<ImageView>(R.id.imageViewProfilePicButton)
        val money = findViewById<TextView>(R.id.textViewPlayerMoney)
        val buttonOpenPacks = findViewById<Button>(R.id.buttonCollection)
        val buttonBuyPacks = findViewById<Button>(R.id.buttonBuyPacks)
        val buttonCollection = findViewById<Button>(R.id.buttonCollection)
        val featured1 = findViewById<ImageView>(R.id.imageViewFeatured1)
        val featured2 = findViewById<ImageView>(R.id.imageViewFeatured2)
        val featured3 = findViewById<ImageView>(R.id.imageViewFeatured3)
        val inboxImage = findViewById<ImageView>(R.id.imageViewInboxButton)
        val homeImage = findViewById<ImageView>(R.id.imageViewHomeButton)
        val storeImage = findViewById<ImageView>(R.id.imageViewStoreButton)

        val border = GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            setStroke(4, Color.BLACK)
            cornerRadius = 12f
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            buttonOpenPacks.foreground = border
        } else {
            val layeredDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#9B597A"))
                setStroke(4, Color.BLACK)
                cornerRadius = 12f
            }
            buttonOpenPacks.background = layeredDrawable
        }



    }
}
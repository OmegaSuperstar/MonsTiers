package com.example.monstiers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(var cards: List<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    inner class CardViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardName: TextView = holder.itemView.findViewById(R.id.cardName)
        val cardNum: TextView = holder.itemView.findViewById(R.id.cardNum)
        val cardTier: TextView = holder.itemView.findViewById(R.id.cardTier)
        val cardType1: TextView = holder.itemView.findViewById(R.id.cardTypes)


        cardName.text = cards[position].cardName
        cardNum.text = cards[position].cardNum.toString()
        cardTier.text = cards[position].cardTier.toString()
        cardType1.text = cards[position].cardType1 + cards[position].cardType2

    }

    override fun getItemCount(): Int {
        return cards.size
    }
}
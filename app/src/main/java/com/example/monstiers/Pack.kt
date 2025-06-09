package com.example.monstiers

data class Pack(
    val id: String,
    val packName: String,
    val cardCount: Int,
    val price: Long,
    val cardIds: List<String>
)

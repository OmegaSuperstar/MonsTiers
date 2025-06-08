package com.example.monstiers

data class Pack(
    val packName: String = "",
    val cardCount: Int = 0,
    val numbersOpened: Int = 0,
    val price: Long = 0L,
    val cardIds: List<String> = emptyList()
)

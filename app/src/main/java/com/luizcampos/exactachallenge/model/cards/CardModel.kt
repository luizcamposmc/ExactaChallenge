package com.luizcampos.exactachallenge.model.cards

data class CardModel(
    val code: String,
    val image: String,
    val images: ImagesModel,
    val suit: String,
    val value: String
)
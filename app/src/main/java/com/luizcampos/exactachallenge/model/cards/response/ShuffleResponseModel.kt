package com.luizcampos.exactachallenge.model.cards.response

data class ShuffleResponseModel(
    val deck_id: String,
    val remaining: Int,
    val shuffled: Boolean,
    val success: Boolean
)
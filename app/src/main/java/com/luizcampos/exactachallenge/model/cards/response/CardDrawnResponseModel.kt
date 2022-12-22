package com.luizcampos.exactachallenge.model.cards.response

import com.luizcampos.exactachallenge.model.cards.CardModel

data class CardDrawnResponseModel(
    val cards: List<CardModel>,
    val deck_id: String,
    val remaining: Int,
    val success: Boolean
)
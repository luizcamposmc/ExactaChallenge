package com.luizcampos.exactachallenge.repository

import com.luizcampos.exactachallenge.helper.Resource
import com.luizcampos.exactachallenge.model.cards.request.ShuffleRequestModel
import com.luizcampos.exactachallenge.model.cards.response.CardDrawnResponseModel
import com.luizcampos.exactachallenge.model.cards.response.ShuffleResponseModel

interface CardRepository {
    suspend fun shuffleCards(shuffleRequestModel: ShuffleRequestModel): Resource<ShuffleResponseModel>

    suspend fun drawCard(deckId: String, count: Int): Resource<CardDrawnResponseModel>
}
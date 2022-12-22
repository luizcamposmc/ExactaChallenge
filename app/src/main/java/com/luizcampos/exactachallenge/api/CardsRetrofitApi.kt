package com.luizcampos.exactachallenge.api

import com.luizcampos.exactachallenge.model.cards.response.CardDrawnResponseModel
import com.luizcampos.exactachallenge.model.cards.request.ShuffleRequestModel
import com.luizcampos.exactachallenge.model.cards.response.ShuffleResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CardsRetrofitApi {

    @POST("new")
    suspend fun shuffleCards(
        @Body shuffleRequestModel: ShuffleRequestModel = ShuffleRequestModel(
            jokers_enabled = true
        )
    ): Response<ShuffleResponseModel>

    @GET("{deckId}/draw")
    suspend fun drawCard(
        @Path("deckId") deckId: String,
        @Query("count") count: Int = 1
    ): Response<CardDrawnResponseModel>
}
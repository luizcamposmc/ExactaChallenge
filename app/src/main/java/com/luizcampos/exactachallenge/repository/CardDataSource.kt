package com.luizcampos.exactachallenge.repository

import com.luizcampos.exactachallenge.api.CardsRetrofitApi
import com.luizcampos.exactachallenge.helper.Resource
import com.luizcampos.exactachallenge.helper.Utils
import com.luizcampos.exactachallenge.model.cards.request.ShuffleRequestModel
import com.luizcampos.exactachallenge.model.cards.response.CardDrawnResponseModel
import com.luizcampos.exactachallenge.model.cards.response.ShuffleResponseModel
import javax.inject.Inject

class CardDataSource @Inject constructor(
    private val cardsRetrofitApi: CardsRetrofitApi
): CardRepository {
    override suspend fun shuffleCards(shuffleRequestModel: ShuffleRequestModel): Resource<ShuffleResponseModel> {
        return try {
            val response = cardsRetrofitApi.shuffleCards(shuffleRequestModel)
            val result = response.body()

            when(response.isSuccessful && result != null) {
                true -> Resource.Success(result)
                false -> Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred\n${Utils.dumpStackTrace(e.stackTrace)}")
        }
    }

    override suspend fun drawCard(deckId: String, count: Int): Resource<CardDrawnResponseModel> {
        return try {
            val response = cardsRetrofitApi.drawCard(deckId, count)
            val result = response.body()

            when(response.isSuccessful && result != null) {
                true -> Resource.Success(result)
                false -> Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred\n${Utils.dumpStackTrace(e.stackTrace)}")
        }
    }
}
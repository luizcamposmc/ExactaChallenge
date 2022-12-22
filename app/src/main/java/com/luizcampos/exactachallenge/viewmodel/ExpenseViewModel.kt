package com.luizcampos.exactachallenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luizcampos.exactachallenge.helper.FlowEvent
import com.luizcampos.exactachallenge.model.Expense
import com.luizcampos.exactachallenge.model.Tags
import com.luizcampos.exactachallenge.model.cards.request.ShuffleRequestModel
import com.luizcampos.exactachallenge.model.cards.response.ShuffleResponseModel
import com.luizcampos.exactachallenge.repository.CardRepository
import com.luizcampos.exactachallenge.repository.ExpenseRepository
import com.luizcampos.exactachallenge.viewmodel.registration.RegistrationViewParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val repository: CardRepository
) : ViewModel() {

    private val _tags = MutableLiveData<Tags>()
    val tags: LiveData<Tags>
        get() = _tags

    private val _currentEntryTime = MutableLiveData<Long>()
    val currentEntryTime: LiveData<Long>
        get() = _currentEntryTime

    private val _onChooseTimeShow = MutableLiveData<Calendar>()
    val onChooseTimeShow
        get() = _onChooseTimeShow

    private val _onChooseDateShow = MutableLiveData<Calendar>()
    val onChooseDateShow
        get() = _onChooseDateShow

    private val _expenses = MutableStateFlow<FlowEvent>(FlowEvent.Empty)
    val expenses: StateFlow<FlowEvent> = _expenses

    private val _cards = MutableStateFlow<FlowEvent>(FlowEvent.Empty)
    val cards: StateFlow<FlowEvent> = _cards

    private var shuffleResponseModel: ShuffleResponseModel? = null

    init {
        viewModelScope.launch {
            val cTime = System.currentTimeMillis()
            _currentEntryTime.value =  cTime
            _onChooseTimeShow.value = Calendar.getInstance().apply {
                timeInMillis = cTime
            }
            _onChooseDateShow.value = Calendar.getInstance().apply {
                timeInMillis = cTime
            }
        }
    }

    fun setTags(value: Tags) {
        _tags.postValue(value)
    }

    fun selectDateTime(time: Long) {
        _currentEntryTime.value = time
    }

    private fun getCurrentEntryDateTime(): Calendar {
        val time = _currentEntryTime.value ?: throw Exception("Any Time is Not Selected Yet!")
        return Calendar.getInstance().apply {
            timeInMillis = time
        }
    }

    fun selectTime(hour: Int, min: Int, milliSec: Int) {
        val selectedTimeMilli = _currentEntryTime.value ?: throw Exception("time is not selected yet")

        val time = Calendar.getInstance().apply {
            timeInMillis = selectedTimeMilli
        }
        time[Calendar.HOUR_OF_DAY] = hour
        time[Calendar.MINUTE] = min
        time[Calendar.MILLISECOND] = milliSec

        _currentEntryTime.value = time.timeInMillis
    }

    private fun updateSelectedDateAsCurrentTime() {
        _currentEntryTime.postValue(Date().time)
    }

    fun onDateSelect() {
        val date = getCurrentEntryDateTime()
        _onChooseDateShow.value = date
    }

    fun onTimeSelect() {
        val time = getCurrentEntryDateTime()
        _onChooseTimeShow.value = time
    }

    fun getExpenses() {
        _expenses.value = FlowEvent.Loading

        viewModelScope.launch(Dispatchers.IO) {
            _expenses.value = getAll()
        }
    }

    fun saveExpense(registrationViewParams: RegistrationViewParams) {
        _expenses.value = FlowEvent.Loading

        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.createExpense(registrationViewParams)

            _expenses.value = getAll()
        }
    }

    fun deleteExpense(id: Long) {
        _expenses.value = FlowEvent.Loading

        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.deleteExpense(id)

            _expenses.value = getAll()
        }
    }

    fun getExpense(id: Long) {
        _expenses.value = FlowEvent.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val expense = expenseRepository.getExpense(id)

            if (expense == null) {
                _expenses.value = FlowEvent.Error("ID '$id' não encontrado!")
            } else {
                _expenses.value = FlowEvent.Success(expense)
            }
        }
    }

    fun nextImage() {
        _cards.value = FlowEvent.Loading

        viewModelScope.launch(Dispatchers.IO) {
            if (
                shuffleResponseModel == null ||
                shuffleResponseModel?.remaining == 0
            ) {
                val shuffleCards = repository.shuffleCards(
                    ShuffleRequestModel(jokers_enabled = true)
                )

                if (shuffleCards.data == null) {
                    _cards.value = FlowEvent.Error(shuffleCards.message!!)
                } else {
                    shuffleResponseModel = shuffleCards.data
                }
            }

            val cardsDrawn = repository.drawCard(
                deckId = shuffleResponseModel!!.deck_id,
                count = 1
            )

            if (cardsDrawn.data == null) {
                _cards.value = FlowEvent.Error(cardsDrawn.message!!)
            } else {
                val uri = cardsDrawn.data.cards[0].image

                _cards.value = FlowEvent.Success(uri)
            }
        }
    }

    private suspend fun getAll() : FlowEvent.Success<List<Expense>>{
        val list = expenseRepository.getAll()

        if (list.isNullOrEmpty()) {
            return FlowEvent.Success(listOf())
        }

        return FlowEvent.Success(list)
    }
}
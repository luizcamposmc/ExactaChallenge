package com.luizcampos.exactachallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.luizcampos.exactachallenge.repository.ExpenseRepository

class ExpenseViewModelFactory constructor(private val expenseRepository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            ExpenseViewModel(this.expenseRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
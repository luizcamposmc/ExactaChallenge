package com.luizcampos.exactachallenge.repository

import com.luizcampos.exactachallenge.model.Expense
import com.luizcampos.exactachallenge.viewmodel.registration.RegistrationViewParams

interface ExpenseRepository {

    suspend fun createExpense(registrationViewParams: RegistrationViewParams)

    suspend fun getAll(): List<Expense>?

    suspend fun getExpense(id: Long): Expense?

    suspend fun deleteExpense(id: Long)
}
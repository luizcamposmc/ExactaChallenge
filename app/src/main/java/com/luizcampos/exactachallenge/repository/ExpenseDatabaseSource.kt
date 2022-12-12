package com.luizcampos.exactachallenge.repository

import com.luizcampos.exactachallenge.model.Expense
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao
import com.luizcampos.exactachallenge.model.database.toExpense
import com.luizcampos.exactachallenge.model.database.toExpenseEntity
import com.luizcampos.exactachallenge.viewmodel.registration.RegistrationViewParams
import javax.inject.Inject

class ExpenseDatabaseSource
@Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {
    override suspend fun createExpense(registrationViewParams: RegistrationViewParams) =  dao.save(registrationViewParams.toExpenseEntity())

    override suspend fun getAll(): List<Expense> = dao.getAll().map { expenseEntity ->  expenseEntity.toExpense() }

    override suspend fun getExpense(id: Long): Expense = dao.getExpense(id).toExpense()

    override suspend fun deleteExpense(id: Long) = dao.deleteExpense(id)
}
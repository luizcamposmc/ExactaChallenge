package com.luizcampos.exactachallenge.repository

import com.luizcampos.exactachallenge.model.Expense
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao
import com.luizcampos.exactachallenge.model.database.toExpense
import com.luizcampos.exactachallenge.model.database.toExpenseEntity
import com.luizcampos.exactachallenge.viewmodel.registration.RegistrationViewParams

class ExpenseDbDataSource (private val expenseDao: ExpenseDao) : ExpenseRepository {
    override suspend fun createExpense(registrationViewParams: RegistrationViewParams) {
        val expenseEntity = registrationViewParams.toExpenseEntity()
        expenseDao.save(expenseEntity)
    }

    override suspend fun getAll(): List<Expense>? {
        return expenseDao.getAll()?.map { expenseEntity ->  expenseEntity.toExpense() }
    }

    override suspend fun getExpense(id: Long): Expense? {
        return expenseDao.getExpense(id)?.toExpense()
    }

    override suspend fun deleteExpense(id: Long) {
        expenseDao.deleteExpense(id)
    }
}
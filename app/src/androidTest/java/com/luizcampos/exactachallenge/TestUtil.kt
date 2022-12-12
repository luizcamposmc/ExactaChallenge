package com.luizcampos.exactachallenge

import com.luizcampos.exactachallenge.model.database.ExpenseEntity
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao
import com.luizcampos.exactachallenge.repository.ExpenseRepository
import com.luizcampos.exactachallenge.viewmodel.registration.RegistrationViewParams
import java.util.*

class TestUtil {

    companion object {
        suspend fun save3Entities(expenseDao: ExpenseDao) {
            expenseDao.save(
                ExpenseEntity(
                    name = "luiz",
                    debtDate = Calendar.getInstance().timeInMillis,
                    amount = 100,
                    tags = "#gastando",
                    description = "g1"
                )
            )

            expenseDao.save(
                ExpenseEntity(
                    name = "otavio",
                    debtDate = Calendar.getInstance().timeInMillis,
                    amount = 1000,
                    tags = "#gastandoMuito",
                    description = "g2"
                )
            )

            expenseDao.save(
                ExpenseEntity(
                    name = "campos",
                    debtDate = Calendar.getInstance().timeInMillis,
                    amount = 50,
                    tags = "#gastandoPouco",
                    description = "g3"
                )
            )
        }

        suspend fun save3Expenses(expenseRepository: ExpenseRepository) {
            expenseRepository.createExpense(
                RegistrationViewParams(
                    name = "luiz",
                    debtDate = Calendar.getInstance().timeInMillis.toString(),
                    amount = "100",
                    tags = "#gastando",
                    description = "g1"
                )
            )

            expenseRepository.createExpense(
                RegistrationViewParams(
                    name = "otavio",
                    debtDate = Calendar.getInstance().timeInMillis.toString(),
                    amount = "1000",
                    tags = "#gastandoMuito",
                    description = "g2"
                )
            )

            expenseRepository.createExpense(
                RegistrationViewParams(
                    name = "campos",
                    debtDate = Calendar.getInstance().timeInMillis.toString(),
                    amount = "50",
                    tags = "#gastandoPouco",
                    description = "g3"
                )
            )
        }
    }
}
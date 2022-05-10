package com.luizcampos.exactachallenge.model.database.dao

import androidx.room.*
import com.luizcampos.exactachallenge.model.database.ExpenseEntity

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(expense: ExpenseEntity)

    @Query("SELECT * FROM expense")
    suspend fun getAll(): List<ExpenseEntity>

    @Query("SELECT * FROM expense WHERE expense_id = :id")
    suspend fun getExpense(id: Long): ExpenseEntity

    @Query("DELETE FROM expense WHERE expense_id = :id")
    suspend fun deleteExpense(id: Long)
}
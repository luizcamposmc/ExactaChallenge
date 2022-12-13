package com.luizcampos.exactachallenge.model.database.dao

import androidx.room.*
import com.luizcampos.exactachallenge.helper.Constants.TABLE_NAME
import com.luizcampos.exactachallenge.model.database.ExpenseEntity

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(expense: ExpenseEntity)

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAll(): List<ExpenseEntity>?

    @Query("SELECT * FROM $TABLE_NAME WHERE expense_id = :id")
    suspend fun getExpense(id: Long): ExpenseEntity?

    @Query("DELETE FROM $TABLE_NAME WHERE expense_id = :id")
    suspend fun deleteExpense(id: Long)
}
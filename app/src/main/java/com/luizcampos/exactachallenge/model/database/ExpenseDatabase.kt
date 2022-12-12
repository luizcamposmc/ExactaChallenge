package com.luizcampos.exactachallenge.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao

@Database(entities = [ExpenseEntity::class], version = 1)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
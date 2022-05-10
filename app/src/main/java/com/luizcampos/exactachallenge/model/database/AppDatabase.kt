package com.luizcampos.exactachallenge.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao

@Database(entities = [ExpenseEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database")
//                    .createFromAsset("database/expense.db") // test
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
package com.luizcampos.exactachallenge.di

import android.content.Context
import androidx.room.Room
import com.luizcampos.exactachallenge.helper.Constants.TABLE_NAME
import com.luizcampos.exactachallenge.model.database.ExpenseDatabase
import com.luizcampos.exactachallenge.model.database.ExpenseEntity
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao
import com.luizcampos.exactachallenge.repository.ExpenseDatabaseSource
import com.luizcampos.exactachallenge.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, ExpenseDatabase::class.java, TABLE_NAME)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideDao(db: ExpenseDatabase) = db.expenseDao()

    @Provides
    fun provideEntity() = ExpenseEntity()

    @Provides
    fun provideExpenseRepository(dao: ExpenseDao) : ExpenseRepository = ExpenseDatabaseSource(dao)
}
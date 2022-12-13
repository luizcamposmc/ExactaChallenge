package com.luizcampos.exactachallenge.di

import android.content.Context
import android.widget.Toast
import androidx.room.Room
import com.luizcampos.exactachallenge.helper.Constants.TABLE_NAME
import com.luizcampos.exactachallenge.helper.ToastDurationProvider
import com.luizcampos.exactachallenge.model.database.ExpenseDatabase
import com.luizcampos.exactachallenge.model.database.ExpenseEntity
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao
import com.luizcampos.exactachallenge.repository.ExpenseDatabaseSource
import com.luizcampos.exactachallenge.repository.ExpenseRepository
import com.luizcampos.exactachallenge.repository.ToastRepository
import com.luizcampos.exactachallenge.repository.ToastyDataSource
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

    @Provides
    @Singleton
    fun provideToastRepository(
        @ApplicationContext context: Context,
        toastDurationProvider: ToastDurationProvider
    ) : ToastRepository = ToastyDataSource(context, toastDurationProvider)

    @Provides
    @Singleton
    fun provideToastDuration() : ToastDurationProvider = object : ToastDurationProvider {
        override val LENGTH_SHORT: Int
            get() = Toast.LENGTH_SHORT

        override val LENGTH_LONG: Int
            get() = Toast.LENGTH_LONG

        override val LENGTH_1S: Int
            get() = 1000

        override val LENGTH_500MS: Int
            get() = 500
    }
}
package com.luizcampos.exactachallenge

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.luizcampos.exactachallenge.model.Expense
import com.luizcampos.exactachallenge.model.database.ExpenseDatabase
import com.luizcampos.exactachallenge.model.database.ExpenseEntity
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao
import com.luizcampos.exactachallenge.repository.ExpenseDbDataSource
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ExpenseEntityTest {
    private lateinit var expenseDao: ExpenseDao
    private lateinit var expenseDatabase: ExpenseDatabase
    private lateinit var expenseDbDataSource: ExpenseDbDataSource

    private val context: Context by lazy {
        ApplicationProvider.getApplicationContext()
    }

    @Before
    fun createDb() {
        expenseDatabase = Room.inMemoryDatabaseBuilder(
            context, ExpenseDatabase::class.java).build()
        expenseDao = expenseDatabase.expenseDao()
        expenseDbDataSource = ExpenseDbDataSource(expenseDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        expenseDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeExpenseAndReadInListDao() {
        TestUtil.save3Entities(expenseDao)

        CoroutineScope(Dispatchers.Main).launch {
            val expenseList = async {
                expenseDao.getAll()
            }.await()

            Log.d("luiz", expenseList.toString())

            val expenseEntity =
                withContext(Dispatchers.Default) {
                    expenseDao.getExpense(2)
                }

            Log.d("luiz", expenseEntity.toString())

            Assert.assertEquals(expenseEntity.name, "otavio")

            Assert.assertEquals(expenseEntity.javaClass, ExpenseEntity::class.java)
        }
    }

    @Test
    @Throws(Exception::class)
    fun writeExpenseAndReadInListExpenseDbDataSource() {
        TestUtil.save3Expenses(expenseDbDataSource)

        CoroutineScope(Dispatchers.Main).launch {
            val expenseList = async {
                expenseDbDataSource.getAll()
            }.await()

            Log.d("luiz", expenseList.toString())

            val expense = withContext(Dispatchers.Default) {
                expenseDbDataSource.getExpense(3)
            }

            Log.d("luiz", expense.toString())

            Assert.assertEquals(expense?.name, "campos")

            Assert.assertEquals(expense?.javaClass, Expense::class.java)
        }
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.luizcampos.exactachallenge", appContext.packageName)
    }
}
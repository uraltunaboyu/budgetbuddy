package com.tunaboyu.budgetbuddy

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tunaboyu.budgetbuddy.model.Budget
import com.tunaboyu.budgetbuddy.model.Transaction
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
  private lateinit var budgetDao: BudgetDao
  private lateinit var transactionDao: TransactionDao
  private lateinit var db: AppDatabase
  
  companion object {
    private val exampleTransaction = Transaction("10/10/10", 10, "")
  }
  
  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    Log.d("BudgetTests", "Loaded context")
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries().fallbackToDestructiveMigration().build()
    budgetDao = db.budgetDao()
    transactionDao = db.transactionDao()
  }
  
  @After
  @Throws(IOException::class)
  fun closeDb() {
    db.close()
  }
  
  @Test
  fun testWritingAndReadingBudget() {
    val budget = Budget(20)
    budgetDao.save(budget)
    val savedBudget = budgetDao.load()
    assertNotNull(savedBudget)
    assertEquals(savedBudget!!.getRemainingFunds(), budget.getRemainingFunds())
  }
  
  @Test
  fun testUpdatingAndReadingBudget() {
    val budget = Budget(20)
    budgetDao.save(budget)
    val savedBudget = budgetDao.load()!!
    budget.setFunds(10)
    budgetDao.save(budget)
    val updatedBudget = budgetDao.load()!!
    assertEquals(savedBudget.getRemainingFunds(), updatedBudget.getRemainingFunds())
  }
  
  @Test
  fun testDeletingBudget() {
    val budget = Budget(20)
    budgetDao.save(budget)
    assertNotNull(budgetDao.load())
    budgetDao.delete(budget)
    assertNull(budgetDao.load())
  }
  
  @Test
  fun testWritingAndReadingTransaction() {
    transactionDao.save(exampleTransaction)
    val savedTransaction = transactionDao.loadAll()
    assertEquals(1, savedTransaction.size)
    assertEquals(exampleTransaction, savedTransaction[0])
  }
  
  @Test
  fun testDeletingTransaction() {
    transactionDao.save(exampleTransaction)
    val savedTransaction = transactionDao.loadAll()
    assertEquals(1, savedTransaction.size)
    transactionDao.delete(exampleTransaction)
    assertTrue(transactionDao.loadAll().isEmpty())
  }
}
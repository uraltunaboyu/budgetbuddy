package com.tunaboyu.budgetbuddy

import com.tunaboyu.budgetbuddy.model.Budget
import com.tunaboyu.budgetbuddy.model.Transaction
import com.tunaboyu.budgetbuddy.util.Converters
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BudgetTransactionTest {
  @Test
  fun transactionGenerates() {
    val transactionDate = LocalDate.parse("25/10/20", Converters.formatter)
    val transactionCost = 10
    val transactionMemo = "test memo"
    val transaction = Transaction(transactionDate, transactionCost, transactionMemo)
    
    assertEquals(transactionDate, transaction.date)
    assertEquals(transactionCost, transaction.cost)
    assertEquals(transactionMemo, transaction.memo)
  }
  
  @Test
  fun budgetGenerates() {
    val budget = Budget(0)
    
    assertEquals(0, budget.getRemainingFunds())
    
    budget.setFunds(10)
    assertEquals(10, budget.getRemainingFunds())
  }
  
  @Test
  fun budgetTransacts() {
    val budget = Budget(10)
    val transaction = Transaction(LocalDate.now(), 10, "")
    
    budget.transact(transaction)
    assertEquals(0, budget.getRemainingFunds())
  }
}
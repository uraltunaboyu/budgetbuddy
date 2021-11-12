package com.tunaboyu.budgetbuddy.db.dao

import androidx.room.*
import com.tunaboyu.budgetbuddy.model.Transaction

@Dao
interface TransactionDao {
  @Query("SELECT * FROM `transaction` ORDER BY date ASC")
  fun loadAll(): List<Transaction>
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(vararg transaction: Transaction)
  
  @Delete
  fun delete(transaction: Transaction)
  
  @Query("DELETE FROM `transaction`")
  fun deleteAll()
}
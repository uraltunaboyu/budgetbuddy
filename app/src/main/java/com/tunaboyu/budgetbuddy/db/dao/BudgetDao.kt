package com.tunaboyu.budgetbuddy.db.dao

import androidx.room.*
import com.tunaboyu.budgetbuddy.model.Budget

@Dao
interface BudgetDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(budget: Budget)
  
  @Delete
  fun delete(budget: Budget)
  
  @Query("SELECT * FROM budget ORDER BY uid ASC LIMIT 1")
  fun load(): Budget?
  
  @Query("DELETE FROM budget")
  fun deleteAll()
}
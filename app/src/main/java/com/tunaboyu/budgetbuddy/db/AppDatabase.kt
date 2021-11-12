package com.tunaboyu.budgetbuddy.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tunaboyu.budgetbuddy.db.dao.BudgetDao
import com.tunaboyu.budgetbuddy.db.dao.TransactionDao
import com.tunaboyu.budgetbuddy.model.Budget
import com.tunaboyu.budgetbuddy.model.Transaction
import com.tunaboyu.budgetbuddy.util.Converters

@Database(entities = [Transaction::class, Budget::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun transactionDao(): TransactionDao
  abstract fun budgetDao(): BudgetDao
  
  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null
    
    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "budget-db"
        ).fallbackToDestructiveMigration()
          .allowMainThreadQueries().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
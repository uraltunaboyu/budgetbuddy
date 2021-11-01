package com.example.budgetbuddy

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.room.*
import com.example.budgetbuddy.databinding.ActivityMainBinding
import java.util.ArrayList

@Entity
class Budget(private var remainingFunds: Int) {
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
    var transactionId = 1

    fun getRemainingFunds() = remainingFunds

    fun setFunds(funds: Int) {
        this.remainingFunds = funds
    }

    fun setTransactions(transactions: List<Transaction>) {
        Log.i("BudgetBuddy", "Loading transactions $transactions")
        if (transactions.isNotEmpty()) {
            this.transactionId = transactions.last().uid
        }
    }

    fun transact(transaction: Transaction) {
        setFunds(getRemainingFunds() - transaction.cost)
    }
}

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

@Database(entities = [Transaction::class, Budget::class], version = 2, exportSchema = false)
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
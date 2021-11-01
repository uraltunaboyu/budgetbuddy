package com.example.budgetbuddy

import androidx.room.*

@Entity
class Transaction(var date: String,
                  var cost: Int,
                  var memo: String) {
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
}


@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction` ORDER BY uid ASC")
    fun loadAll(): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("DELETE FROM `transaction`")
    fun deleteAll()
}


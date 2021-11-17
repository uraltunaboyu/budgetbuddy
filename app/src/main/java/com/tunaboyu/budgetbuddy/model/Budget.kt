package com.tunaboyu.budgetbuddy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Budget(private var remainingFunds: Float) {
  @PrimaryKey(autoGenerate = true)
  var uid: Int = 1
  
  fun getRemainingFunds() = remainingFunds
  
  fun setFunds(funds: Float) {
    this.remainingFunds = funds
  }
  
  fun transact(transaction: Transaction) {
    if (transaction.cost > remainingFunds) {
      setFunds(0F)
    } else {
      setFunds(remainingFunds - transaction.cost)
    }
  }
}

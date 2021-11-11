package com.tunaboyu.budgetbuddy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Budget(private var remainingFunds: Int) {
  @PrimaryKey(autoGenerate = true)
  var uid: Int = 1
  
  fun getRemainingFunds() = remainingFunds
  
  fun setFunds(funds: Int) {
    this.remainingFunds = funds
  }
  
  fun transact(transaction: Transaction) {
    setFunds(getRemainingFunds() - transaction.cost)
  }
}

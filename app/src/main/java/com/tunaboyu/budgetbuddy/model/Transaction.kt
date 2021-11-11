package com.tunaboyu.budgetbuddy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Transaction(
  var date: String,
  var cost: Int,
  var memo: String
) {
  @PrimaryKey(autoGenerate = true)
  var uid: Int = 0
}

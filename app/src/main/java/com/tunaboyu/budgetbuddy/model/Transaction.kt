package com.tunaboyu.budgetbuddy.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
class Transaction(
  var date: LocalDate,
  var cost: Float,
  var memo: String
) {
  @PrimaryKey(autoGenerate = true)
  var uid: Int = 0
}

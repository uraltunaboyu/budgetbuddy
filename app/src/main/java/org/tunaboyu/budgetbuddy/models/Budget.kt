package org.tunaboyu.budgetbuddy.models;
import android.graphics.Color
import java.util.UUID

data class Budget(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val limit: Double,
    val color: Int = Color.rgb((0..255).random(), (0..255).random(), (0..255).random()),
    var remainingAmount: Double = limit,
    val transactions: MutableList<Transaction> = mutableListOf()
)
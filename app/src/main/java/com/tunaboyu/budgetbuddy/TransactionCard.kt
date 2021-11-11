package com.tunaboyu.budgetbuddy

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView

class TransactionCard(context: Context, editFunction: () -> Unit, deleteFunction: () -> Unit): CardView(context, null) {
    init {
        View.inflate(context, R.layout.card_transaction, this)
        findViewById<TextView>(R.id.dateCell).rootView.setOnTouchListener(TransactionCardTouchListener(this, context, editFunction, deleteFunction))
    }
    
    fun setTransaction(transaction: Transaction) {
        findViewById<TextView>(R.id.dateCell).text = transaction.date
        findViewById<TextView>(R.id.costCell).text = transaction.cost.toString()
        findViewById<TextView>(R.id.memoCell).text = transaction.memo
    }
}
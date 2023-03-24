package org.tunaboyu.budgetbuddy

import androidx.recyclerview.widget.RecyclerView

interface OnBudgetDragListener {
    fun onBudgetDrag(viewHolder: RecyclerView.ViewHolder)
}
package com.tunaboyu.budgetbuddy.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.tunaboyu.budgetbuddy.R
import com.tunaboyu.budgetbuddy.model.Transaction
import com.tunaboyu.budgetbuddy.util.Converters
import com.tunaboyu.budgetbuddy.util.Filter
import java.time.LocalDate

class EditTransactionDialogFragment(private val transaction: Transaction) : DialogFragment(), DatePickerFragment.DatePickerFragmentListener {
  private lateinit var listener: EditTransactionDialogListener
  private lateinit var newDate: TextView
  private lateinit var newCost: EditText
  private lateinit var newMemo: EditText
  
  interface EditTransactionDialogListener {
    fun saveTransaction(transaction: Transaction)
    fun addFunds(funds: Int)
  }
  
  override fun setDate(date: String) {
    newDate.text = date
  }
  
  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      listener = context as EditTransactionDialogListener
    } catch (e: ClassCastException) {
      throw ClassCastException("$context must implement EditTransactionDialogListener")
    }
  }
  
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      val builder = AlertDialog.Builder(it)
      val inflater = requireActivity().layoutInflater
      val view = inflater.inflate(R.layout.dialog_edit_transaction, null)
      newDate = view.findViewById(R.id.edit_transaction_date)
      newCost = view.findViewById(R.id.edit_transaction_cost)
      newMemo = view.findViewById(R.id.edit_transaction_memo)
      newDate.text = transaction.date.format(Converters.formatter)
      newCost.filters = arrayOf(Filter.decimalPointFilter)
      newCost.setText(transaction.cost.toString())
      newMemo.setText(transaction.memo)
      
      builder.setView(view)
        .setPositiveButton(
          R.string.save
        ) { _, _ ->
          if (newCost.text.toString() != "") {
            val newTransaction = Transaction(
              LocalDate.parse(newDate.text.toString(), Converters.formatter),
              Integer.parseInt(newCost.text.toString()),
              newMemo.text.toString()
            )
            newTransaction.uid = transaction.uid
            listener.saveTransaction(newTransaction)
            listener.addFunds(transaction.cost - newTransaction.cost)
          }
        }
        .setNegativeButton(
          R.string.cancel
        ) { _, _ ->
        }
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }
}
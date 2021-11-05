package com.tunaboyu.budgetbuddy

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class AddFundsDialogFragment: DialogFragment() {
  internal lateinit var listener: AddFundsDialogListener
  
  interface AddFundsDialogListener {
    fun addFunds(funds: Int)
  }
  
  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      listener = context as AddFundsDialogListener
    } catch (e: ClassCastException) {
      throw ClassCastException("$context must implement AddFundsDialogListener")
    }
  }
  
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      val builder = AlertDialog.Builder(it)
      val inflater = requireActivity().layoutInflater
      val view = inflater.inflate(R.layout.dialog_fund_bump, null)
      val fundsToAdd = view.findViewById<EditText>(R.id.funds_to_add)
      
      builder.setView(view)
        .setPositiveButton(
          R.string.add
        ) { _, _ ->
          if (fundsToAdd.text.toString() != "") {
            listener.addFunds(Integer.parseInt(fundsToAdd.text.toString()))
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
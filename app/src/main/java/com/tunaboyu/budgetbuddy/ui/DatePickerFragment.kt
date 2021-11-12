package com.tunaboyu.budgetbuddy.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {
  lateinit var listener: DatePickerFragmentListener
  
  interface DatePickerFragmentListener {
    fun setDate(date: String)
  }
  
  
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    
    return DatePickerDialog(requireContext(), this, year, month, day)
  }
  
  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    listener.setDate("${if(dayOfMonth < 10) "0" else ""}$dayOfMonth/${if(month < 9) "0" else ""}${month + 1}/${year % 100}")
  }
}
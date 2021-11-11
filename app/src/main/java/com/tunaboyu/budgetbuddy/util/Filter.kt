package com.tunaboyu.budgetbuddy.util

import android.text.InputFilter
import java.util.regex.Pattern

class Filter {
  companion object {
    val lengthFilter = InputFilter { _, _, _, dest, _, _ ->
      return@InputFilter if (dest.length > 30) "" else null
    }
    val decimalPointFilter = InputFilter { source, start, end, dest, dstart, dend ->
      val text = dest.substring(0, dstart) + source.substring(start, end) + dest.substring(
        dend,
        dest.length
      )
      val matcher = Pattern.compile("[0-9]+(.[0-9]{0,2})?").matcher(text)
      return@InputFilter if (!matcher.matches()) "" else null
    }
  }
}
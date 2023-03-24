package org.tunaboyu.budgetbuddy.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import org.tunaboyu.budgetbuddy.R

class AddBudgetFragment : Fragment() {
    private var selectedColor: Int = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_add_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val budgetColorView: View = view.findViewById(R.id.color_picker)
        budgetColorView.setBackgroundColor(selectedColor)
        budgetColorView.setOnClickListener {
            showColorPickerDialog()
        }
    }

    private fun showColorPickerDialog() {
        val colorPickerDialog = ColorPickerDialog.Builder(requireContext())
            .setTitle("Choose Budget Color")
            .setPreferenceName("budget_color_picker")
            .attachAlphaSlideBar(false)
            .attachBrightnessSlideBar(false)
            .setPositiveButton("Confirm") { _, _ -> }
            .setNegativeButton("Cancel") { _, _ -> }

        colorPickerDialog.colorPickerView.setColorListener(ColorEnvelopeListener { envelope: ColorEnvelope?, _: Boolean ->
                selectedColor = envelope!!.color
                view?.findViewById<View>(R.id.color_picker)?.setBackgroundColor(selectedColor)
        })

        colorPickerDialog.show()
    }
}

class AddTransactionFragment : Fragment() {

    private lateinit var transactionDateEditText: EditText
    private lateinit var transactionTimeEditText: EditText
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add_transaction, container, false)

        transactionDateEditText = view.findViewById(R.id.transaction_date)
        setDateEditText(calendar)
        transactionDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        transactionTimeEditText = view.findViewById(R.id.transaction_time)
        setTimeEditText(calendar)
        transactionTimeEditText.setOnClickListener {
            showTimePickerDialog()
        }

        return view
    }

    private fun setDateEditText(calendar: Calendar) {
        transactionDateEditText.setText(DateFormat.getDateFormat(requireContext()).format(calendar.time))
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                setDateEditText(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setTimeEditText(calendar: Calendar) {
        transactionTimeEditText.setText(DateFormat.getTimeFormat(requireContext()).format(calendar.time))
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                setTimeEditText(calendar)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(requireContext())
        )
        timePickerDialog.show()
    }
}

class FormPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AddTransactionFragment()
            1 -> AddBudgetFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
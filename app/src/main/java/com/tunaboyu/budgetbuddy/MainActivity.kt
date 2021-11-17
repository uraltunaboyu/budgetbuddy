package com.tunaboyu.budgetbuddy

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.button.MaterialButton
import com.tunaboyu.budgetbuddy.databinding.ActivityMainBinding
import com.tunaboyu.budgetbuddy.db.AppDatabase
import com.tunaboyu.budgetbuddy.model.Budget
import com.tunaboyu.budgetbuddy.model.Transaction
import com.tunaboyu.budgetbuddy.ui.DatePickerFragment
import com.tunaboyu.budgetbuddy.ui.EditTransactionDialogFragment
import com.tunaboyu.budgetbuddy.ui.TransactionCard
import com.tunaboyu.budgetbuddy.util.Binding
import com.tunaboyu.budgetbuddy.util.Converters
import com.tunaboyu.budgetbuddy.util.Filter
import java.time.LocalDate


class MainActivity : AppCompatActivity(),
  EditTransactionDialogFragment.EditTransactionDialogListener,
  DatePickerFragment.DatePickerFragmentListener {
  private lateinit var binding: ActivityMainBinding
  private lateinit var budget: Budget
  private lateinit var db: AppDatabase
  private lateinit var editingTransaction: EditTransactionDialogFragment
  private var firstUse = true
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = Binding.initBinding(layoutInflater)
    val view = binding.root
    setContentView(view)
    setSupportActionBar(binding.toolbar)
    db = AppDatabase.getDatabase(applicationContext)
    setFiltersForCostFields()
    if (!loadSavedData()) {
      budget = Budget(0F)
    }
  }
  
  private fun setFiltersForCostFields() {
    binding.transactionMemo.filters = arrayOf(Filter.lengthFilter)
    binding.transactionCost.filters = arrayOf(Filter.decimalPointFilter)
    binding.budgetText.filters = arrayOf(Filter.decimalPointFilter)
  }
  
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.toolbar_menu, menu)
    return true
  }
  
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.nav_clear_data -> {
        clearAllData()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
  
  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
        val budgetEntry = binding.budgetText.text.toString()
        if (budgetEntry != "") {
          if (firstUse) {
            firstTimeSetup(budgetEntry.toFloat())
          } else {
            val transactionEntry = binding.transactionCost.text.toString()
            if (transactionEntry != "") {
              generateTransaction()
            }
          }
        }
        true
      }
      else -> super.onKeyUp(keyCode, event)
    }
  }
  
  private fun clearAllData() {
    db.budgetDao().deleteAll()
    db.transactionDao().deleteAll()
    setFunds(0F)
    binding.transactionTable.removeAllViews()
  }
  
  private fun generateTransaction() {
    val transactionDate = LocalDate.parse(binding.transactionDate.text, Converters.formatter)
    val transactionCost = binding.transactionCost.text.toString().toFloat()
    val transactionMemo = binding.transactionMemo.text.toString()
    addTransaction(Transaction(transactionDate, transactionCost, transactionMemo))
  }
  
  private fun loadSavedData(): Boolean {
    val loadedBudget = db.budgetDao().load()
    if (loadedBudget != null) {
      budget = loadedBudget
      doSetup()
      setFunds()
      buildTransactionTable()
      return true
    }
    return false
  }
  
  private fun buildTransactionTable() {
    binding.transactionTable.removeAllViews()
    val loadedTransactions = db.transactionDao().loadAll()
    loadedTransactions.map { addTransactionToTable(it) }
  }
  
  private fun addTransaction(
    newTransaction: Transaction
  ) {
    db.transactionDao().save(newTransaction)
    budget.transact(newTransaction)
    db.budgetDao().save(budget)
    updateFundsText()
    buildTransactionTable()
  }
  
  private fun addTransactionToTable(transaction: Transaction) {
    var transactionCard: TransactionCard? = null
    transactionCard = TransactionCard(this,
      editFunction = {
        editingTransaction = EditTransactionDialogFragment(transaction)
        editingTransaction.show(supportFragmentManager, "editTransaction")
      },
      deleteFunction = {
        db.transactionDao().delete(transaction)
        binding.transactionTable.removeView(transactionCard)
      })
    transactionCard.setTransaction(transaction)
    binding.transactionTable.addView(transactionCard, 0)
    binding.transactionCost.setText("")
    binding.transactionMemo.setText("")
  }
  
  private fun doSetup() {
    val constraintLayout = binding.root
    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.setVisibility(R.id.transactionCost, View.VISIBLE)
    constraintSet.setVisibility(R.id.transactionMemo, View.VISIBLE)
    constraintSet.setVisibility(R.id.transactionDate, View.VISIBLE)
    constraintSet.connect(
      R.id.budgetText,
      ConstraintSet.BOTTOM,
      R.id.scrollView2,
      ConstraintSet.TOP
    )
    constraintSet.applyTo(constraintLayout)
    binding.transactionDate.text = LocalDate.now().format(Converters.formatter)
    binding.budgetText.hint = ""
    binding.budgetText.textSize = 34F
    binding.budgetText.setOnFocusChangeListener { _, hasFocus ->
      if (!hasFocus) {
        setFunds(binding.budgetText.text.toString().toFloat())
      }
    }
    firstUse = false
  }
  
  private fun firstTimeSetup(funds: Float) {
    doSetup()
    setFunds(funds)
  }
  
  private fun updateFundsText() {
    binding.budgetText.setText("%.2f".format(budget.getRemainingFunds()))
  }
  
  
  override fun saveTransaction(transaction: Transaction) {
    db.transactionDao().save(transaction)
    buildTransactionTable()
  }
  
  override fun addFunds(funds: Float) = setFunds(budget.getRemainingFunds() + funds)
  
  private fun setFunds(funds: Float = budget.getRemainingFunds()) {
    budget.setFunds(funds)
    db.budgetDao().save(budget)
    updateFundsText()
  }
  
  fun showDatePickerDialog(view: View) {
    val newFragment = DatePickerFragment()
    newFragment.listener = if (view is MaterialButton) editingTransaction else this
    newFragment.show(supportFragmentManager, "datePicker")
  }
  
  companion object {
    private const val TAG = "BudgetBuddy"
  }
  
  override fun setDate(date: String) {
    binding.transactionDate.text = date
  }
}

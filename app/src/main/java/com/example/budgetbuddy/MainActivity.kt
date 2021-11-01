package com.example.budgetbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import com.example.budgetbuddy.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var budget: Budget
    private lateinit var db: AppDatabase
    private lateinit var transactionDao: TransactionDao
    private var firstUse = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        db = AppDatabase.getDatabase(applicationContext)
        transactionDao = db.transactionDao()
        Log.i(TAG, "Loaded db")
        if (!loadSavedData()) {
            budget = Budget(0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_clear_data -> {
                clearAllData()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return when(keyCode) {
            KeyEvent.KEYCODE_ENTER , KeyEvent.KEYCODE_NUMPAD_ENTER-> {
                if (binding.budgetEntry.text.toString() != "") {
                    if (firstUse) {
                        firstTimeSetup(Integer.parseInt(binding.budgetEntry.text.toString()))
                    } else {
                        generateTransaction()
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
        budget = Budget(0)
        db.budgetDao().save(budget)
        updateFundsText()
        binding.transactionTable.removeAllViews()
    }

    private fun generateTransaction() {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val transactionDate = LocalDateTime.now().format(dateFormatter)
        val transactionCost = Integer.parseInt(binding.budgetEntry.text.toString())
        val transactionMemo = binding.budgetMemo.text.toString()
        addTransaction(transactionDate, transactionCost, transactionMemo)
    }

    private fun loadSavedData(): Boolean {
        val loadedBudget = db.budgetDao().load()
        val loadedTransactions = db.transactionDao().loadAll()
        if (loadedBudget != null) {
            budget = loadedBudget
            budget.setTransactions(loadedTransactions)
            doSetup()
            loadedTransactions.map { addTransactionToTable(it) }
            return true
        }
        return false
    }

    private fun addTransaction(
        transactionDate: String,
        transactionCost: Int,
        transactionMemo: String
    ) {
        val newTransaction = Transaction(transactionDate, transactionCost, transactionMemo)
        db.transactionDao().save(newTransaction)
        budget.transact(newTransaction)
        db.budgetDao().save(budget)
        updateFundsText()
        addTransactionToTable(newTransaction)
    }

    private fun addTransactionToTable(transaction: Transaction) {
        val dateField = TextView(this)
        dateField.text = transaction.date
        dateField.setPadding(5, 0, 10, 0)
        val costField = TextView(this)
        costField.text = transaction.cost.toString()
        costField.setPadding(10, 0, 5, 0)
        val memoField = TextView(this)
        memoField.text = transaction.memo
        memoField.setPadding(20, 0, 5, 0)
        memoField.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        val newRow = TableRow(this)
        newRow.addView(dateField)
        newRow.addView(costField)
        newRow.addView(memoField)
        Log.i(TAG, "Adding transaction to table")
        binding.transactionTable.addView(newRow)
    }

    private fun doSetup() {
        val constraintLayout = binding.root
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.setHorizontalBias(R.id.budgetEntry, 0.1F)
        constraintSet.setVisibility(R.id.budgetMemo, View.VISIBLE)
        constraintSet.applyTo(constraintLayout)
        binding.budgetText.textSize = 34F
        binding.budgetEntry.hint = "Transaction cost"
        firstUse = false
        updateFundsText()
    }

    private fun firstTimeSetup(funds: Int) {
        budget.setFunds(funds)
        doSetup()
        db.budgetDao().save(budget)
    }

    private fun updateFundsText() {
        binding.budgetText.text = budget.getRemainingFunds().toString()
    }

    companion object {
        private const val TAG = "BudgetBuddy"
    }
}
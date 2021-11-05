package com.tunaboyu.budgetbuddy

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import com.tunaboyu.budgetbuddy.databinding.ActivityMainBinding
import java.lang.ClassCastException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(), AddFundsDialogFragment.AddFundsDialogListener {
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
        return when(item.itemId) {
            R.id.nav_clear_data -> {
                clearAllData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return when(keyCode) {
            KeyEvent.KEYCODE_ENTER , KeyEvent.KEYCODE_NUMPAD_ENTER-> {
                val userEntry = binding.budgetEntry.text.toString()
                if (userEntry != "") {
                    if (firstUse) {
                        firstTimeSetup(Integer.parseInt(userEntry))
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
        setFunds(0)
        binding.transactionTable.removeAllViews()
    }

    private fun generateTransaction() {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val transactionDate = LocalDateTime.now().format(dateFormatter)
        val transactionCost = Integer.parseInt(binding.budgetEntry.text.toString())
        val transactionMemo = binding.budgetMemo.text.toString()
        addTransaction(Transaction(transactionDate, transactionCost, transactionMemo))
    }

    private fun loadSavedData(): Boolean {
        val loadedBudget = db.budgetDao().load()
        val loadedTransactions = db.transactionDao().loadAll()
        if (loadedBudget != null) {
            budget = loadedBudget
            doSetup()
            setFunds()
            loadedTransactions.map { addTransactionToTable(it) }
            return true
        }
        return false
    }

    private fun addTransaction(
        newTransaction: Transaction
    ) {
        db.transactionDao().save(newTransaction)
        budget.transact(newTransaction)
        db.budgetDao().save(budget)
        updateFundsText()
        addTransactionToTable(newTransaction)
    }

    private fun addTransactionToTable(transaction: Transaction) {
        val transactionCard = TransactionCard(this)
        transactionCard.setTransaction(transaction)
        binding.transactionTable.addView(transactionCard)
        binding.budgetEntry.setText("")
        binding.budgetMemo.setText("")
    }

    private fun doSetup() {
        val constraintLayout = binding.root
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.setHorizontalBias(R.id.budgetEntry, 0.1F)
        constraintSet.setVisibility(R.id.budgetMemo, View.VISIBLE)
        constraintSet.setVisibility(R.id.funds_bump, View.VISIBLE)
        constraintSet.applyTo(constraintLayout)
        binding.budgetText.textSize = 34F
        binding.budgetEntry.hint = "Transaction cost"
        binding.budgetEntry.setText("")
        firstUse = false
    }

    private fun firstTimeSetup(funds: Int) {
        doSetup()
        setFunds(funds)
    }

    private fun updateFundsText() {
        binding.budgetText.text = budget.getRemainingFunds().toString()
    }

    // redundant parameter view is expected by the button in activity_main.xml
    fun addFundsDialog(view: View) = AddFundsDialogFragment().show(supportFragmentManager, "add_funds")

    override fun addFunds(funds: Int) = setFunds(budget.getRemainingFunds() + funds)

    private fun setFunds(funds: Int = budget.getRemainingFunds()) {
        budget.setFunds(funds)
        db.budgetDao().save(budget)
        updateFundsText()
    }

    companion object {
        private const val TAG = "BudgetBuddy"
    }
}

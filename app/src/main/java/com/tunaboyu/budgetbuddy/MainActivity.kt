package com.tunaboyu.budgetbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintSet
import com.tunaboyu.budgetbuddy.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern


class MainActivity : AppCompatActivity(), AddFundsDialogFragment.AddFundsDialogListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var budget: Budget
    private lateinit var db: AppDatabase
    private var firstUse = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        db = AppDatabase.getDatabase(applicationContext)
        setFiltersForCostFields()
        if (!loadSavedData()) {
            budget = Budget(0)
        }
    }
    
    private fun setFiltersForCostFields() {
        val decimalPointFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val text = dest.substring(0, dstart) + source.substring(start, end) + dest.substring(
                dend,
                dest.length
            )
            val matcher = Pattern.compile("[0-9]+(.[0-9]{0,2})?").matcher(text)
            return@InputFilter if (!matcher.matches()) "" else null
        }
        val lengthFilter = InputFilter { _, _, _, dest, _, _ ->
            return@InputFilter if (dest.length > 30) "" else null
        }
        binding.transactionMemo.filters = arrayOf(lengthFilter)
        binding.transactionCost.filters = arrayOf(decimalPointFilter)
        binding.budgetText.filters = arrayOf(decimalPointFilter)
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
                val budgetEntry = binding.budgetText.text.toString()
                Log.d(TAG, "Setting budget to $budgetEntry")
                if (budgetEntry != "") {
                    if (firstUse) {
                        firstTimeSetup(Integer.parseInt(budgetEntry))
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
        setFunds(0)
        binding.transactionTable.removeAllViews()
    }

    private fun generateTransaction() {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val transactionDate = LocalDateTime.now().format(dateFormatter)
        val transactionCost = Integer.parseInt(binding.transactionCost.text.toString())
        val transactionMemo = binding.transactionMemo.text.toString()
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
        var transactionCard: TransactionCard? = null
        transactionCard = TransactionCard(this, deleteFunction = {
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
        constraintSet.connect(R.id.budgetText, ConstraintSet.BOTTOM, R.id.scrollView2, ConstraintSet.TOP)
        constraintSet.applyTo(constraintLayout)
        binding.budgetText.hint = ""
        binding.budgetText.textSize = 34F
        binding.budgetText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                setFunds(Integer.parseInt(binding.budgetText.text.toString()))
            }
        }
        firstUse = false
    }

    private fun firstTimeSetup(funds: Int) {
        doSetup()
        setFunds(funds)
    }

    private fun updateFundsText() {
        binding.budgetText.setText(budget.getRemainingFunds().toString())
    }


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

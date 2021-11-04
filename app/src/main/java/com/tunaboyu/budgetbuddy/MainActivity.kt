package com.tunaboyu.budgetbuddy

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
        constraintSet.setVisibility(R.id.funds_bump, View.VISIBLE)
        constraintSet.applyTo(constraintLayout)
        binding.budgetText.textSize = 34F
        binding.budgetEntry.hint = "Transaction cost"
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
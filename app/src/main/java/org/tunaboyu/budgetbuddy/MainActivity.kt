package org.tunaboyu.budgetbuddy

import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.tunaboyu.budgetbuddy.models.Budget
import org.tunaboyu.budgetbuddy.models.Transaction
import org.tunaboyu.budgetbuddy.ui.FormPagerAdapter
import java.util.*

class MainActivity : AppCompatActivity(), OnBudgetDragListener {
    private inner class DragDropItemTouchHelperCallback(private val budgets: MutableList<Budget>) :
        ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.bindingAdapterPosition
            val toPosition = target.bindingAdapterPosition
            Collections.swap(budgets, fromPosition, toPosition)
            budgetAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val offset = if (isCurrentlyActive) dY else 0f
            itemView.translationY = offset
            super.onChildDraw(c, recyclerView, viewHolder, dX, offset, actionState, isCurrentlyActive)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                budgetAdapter.onCardDragStart(viewHolder!!)
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            budgetAdapter.onCardDragEnd(viewHolder)
            super.clearView(recyclerView, viewHolder)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var budgetAdapter: BudgetAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var addBudgetIcon: FloatingActionButton
    private val categoryHolder = CategoryHolder()

    // Sample budgets for testing
    private val sampleBudgets = mutableListOf<Budget>().apply {
        val loyaltyCardCategory = categoryHolder.createCategory("Loyalty Card")

        add(
            Budget(
                name = "Groceries",
                limit = 200.0,
                transactions = mutableListOf(
                    Transaction(location = "Walmart", category = null, amount = 30.0),
                    Transaction(location = "Target", category = loyaltyCardCategory, amount = 25.0)
                )
            )
        )
        add(
            Budget(
                name = "Entertainment",
                limit = 100.0,
                transactions = mutableListOf(
                    Transaction(location = "AMC", category = null, amount = 12.0)
                )
            )
        )
        add(
            Budget(
                name = "Utilities",
                limit = 150.0,
                transactions = mutableListOf(
                    Transaction(location = "Water bill", category = null, amount = 45.0)
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupItemTouchHelper()
        setupFloatingActionButton()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.budget_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        budgetAdapter = BudgetAdapter(sampleBudgets, this, this)
        recyclerView.adapter = budgetAdapter
    }

    private fun setupItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(DragDropItemTouchHelperCallback(
            sampleBudgets
        ))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupFloatingActionButton() {
        addBudgetIcon = findViewById(R.id.add_budget_button);

        addBudgetIcon.setOnClickListener {
            showAddBudgetDialog()
        }
    }

    private fun showAddBudgetDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_viewpager, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val tabLayout: TabLayout = dialogView.findViewById(R.id.tab_layout)
        val viewPager: ViewPager2 = dialogView.findViewById(R.id.view_pager)

        viewPager.adapter = FormPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Add Transaction"
                1 -> "Add Budget"
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }.attach()

        dialog.show()
    }

    override fun onBudgetDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}
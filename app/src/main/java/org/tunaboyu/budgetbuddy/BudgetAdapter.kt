package org.tunaboyu.budgetbuddy
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.gesture.Gesture
import android.graphics.Color
import android.graphics.Rect
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.tunaboyu.budgetbuddy.models.Budget
import java.util.*
import kotlin.math.abs

class BudgetAdapter(
    private val budgets: List<Budget>,
    private val onBudgetDragListener: OnBudgetDragListener,
    private val context: Context
) : RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {
    private var isCardExpanded = false
    private val ANIMATION_DURATION = 300L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.budget_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val budget = budgets[position]

        holder.budgetName.text = budget.name
        holder.budgetRemaining.text = String.format("%.2f", budget.remainingAmount)

        val isDark = ColorUtils.calculateLuminance(budget.color) < 0.5
        val textColor = if (isDark) Color.WHITE else Color.BLACK
        holder.budgetName.setTextColor(textColor)
        holder.budgetRemaining.setTextColor(textColor)

        holder.budgetCard.setCardBackgroundColor(budget.color)

        val progressHeightPercentage = (((budget.limit - budget.remainingAmount) / budget.limit) * 100).toInt()
        val layoutParams = holder.budgetProgress.layoutParams
        layoutParams.height = 0
        holder.budgetProgress.layoutParams = layoutParams
        holder.budgetProgress.setBackgroundColor(ColorUtils.blendARGB(budget.color, Color.BLACK, 0.5f))

        holder.budgetCard.setOnLongClickListener {
            onBudgetDragListener.onBudgetDrag(holder)
            true
        }

        holder.budgetCard.setOnClickListener {
            toggleCard(holder.budgetCard, holder.originalBounds)
        }

        // Display the three most recent transactions
        val recentTransactions = budget.transactions.takeLast(3).reversed()
        holder.recentTransactionsContainer.removeAllViews()
        for (transaction in recentTransactions) {
            val transactionEntryView = LayoutInflater.from(context).inflate(R.layout.transaction_entry, holder.recentTransactionsContainer, false)
            val transactionTextView: TextView = transactionEntryView.findViewById(R.id.transaction_text)
            val categoryTextView: TextView = transactionEntryView.findViewById(R.id.category_text)

            transactionTextView.text = "${transaction.location} - $${transaction.amount}"
            transactionTextView.setTextColor(textColor)
            transaction.category?.let { category ->
                categoryTextView.text = category.toString()
                categoryTextView.visibility = View.VISIBLE
            }

            holder.recentTransactionsContainer.addView(transactionEntryView)
        }
    }

    private fun toggleCard(cardView: CardView, originalBounds: Rect) {
        val contentContainer: View = cardView.findViewById(R.id.content_container)
        val fab: FloatingActionButton = (cardView.context as Activity).findViewById(R.id.add_budget_button)

        if (!isCardExpanded) {
            // Calculate the scale factor for height based on the screen height
            val screenHeight = cardView.context.resources.displayMetrics.heightPixels
            val heightScaleFactor = screenHeight.toFloat() / cardView.height

            // Calculate the translation Y value based on the difference between the top of the card and the top of the screen
            val translationY = -cardView.top.toFloat()

            // Animate the card to fill the screen height
            cardView.animate()
                .setDuration(ANIMATION_DURATION)
                .scaleY(heightScaleFactor)
                .translationY(translationY)
                .z(cardView.z + 1) // Increase the elevation
                .withStartAction {
                    isCardExpanded = true
                }
                .start()

            // Scale the content container inversely to maintain the size and relative placement of the elements
            contentContainer.animate()
                .setDuration(ANIMATION_DURATION)
                .scaleY(1 / heightScaleFactor)
                .start()

            // Animate FAB color change to the cardView's color
            fab.animateBackgroundColor(cardView.cardBackgroundColor.defaultColor, 300)
        } else {
            // Animate the card back to its original position and size
            cardView.animate()
                .setDuration(ANIMATION_DURATION)
                .scaleY(1f)
                .translationY(originalBounds.top.toFloat())
                .z(cardView.z - 1) // Decrease the elevation
                .withEndAction {
                    isCardExpanded = false
                }
                .start()

            // Reset the content container's scale
            contentContainer.animate()
                .setDuration(ANIMATION_DURATION)
                .scaleY(1f)
                .start()

            // Set FAB color to its original color
            fab.animateBackgroundColor(ContextCompat.getColor(cardView.context, R.color.fab_color), 300)
        }
    }

    override fun getItemCount() = budgets.size

    fun onCardDragStart(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.animate().scaleX(1.1f).scaleY(1.1f).duration = 100
    }

    fun onCardDragEnd(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.animate().scaleX(1f).scaleY(1f).duration = 100
    }

    fun FloatingActionButton.animateBackgroundColor(endColor: Int, duration: Long) {
        val startColor = this.backgroundTintList!!.defaultColor
        val colorAnimator = ValueAnimator.ofArgb(startColor, endColor)
        colorAnimator.duration = duration
        colorAnimator.addUpdateListener { animator ->
            this.backgroundTintList = ColorStateList.valueOf(animator.animatedValue as Int)
        }
        colorAnimator.start()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val budgetName: TextView = view.findViewById(R.id.budget_name)
        val budgetRemaining: TextView = view.findViewById(R.id.budget_remaining)
        val recentTransactionsContainer: LinearLayout = view.findViewById(R.id.recent_transactions_container)
        val budgetCard: CardView = view.findViewById(R.id.budget_card)
        val budgetProgress: View = view.findViewById(R.id.budget_progress)
        val originalBounds = Rect(budgetCard.left, budgetCard.top, budgetCard.right, budgetCard.bottom)
    }
}
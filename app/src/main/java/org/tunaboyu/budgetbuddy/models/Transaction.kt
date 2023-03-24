package org.tunaboyu.budgetbuddy.models
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.tunaboyu.budgetbuddy.models.Category
import java.util.UUID

@Entity(foreignKeys =   [ForeignKey(entity = Budget::class,
                                parentColumns =  ["id"],
                                childColumns = ["budgetId"],
                                onDelete = ForeignKey.CASCADE),
                        ForeignKey(entity = Category::class,
                                parentColumns = ["name"],
                                childColumns = ["category"])])
data class Transaction(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val location: String,
    val amount: Double,
    val category: Category?
)
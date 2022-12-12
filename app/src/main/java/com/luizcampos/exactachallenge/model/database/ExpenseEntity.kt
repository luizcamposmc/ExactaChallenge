package com.luizcampos.exactachallenge.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luizcampos.exactachallenge.helper.Constants.TABLE_NAME
import com.luizcampos.exactachallenge.model.Expense
import com.luizcampos.exactachallenge.viewmodel.registration.RegistrationViewParams
import java.util.*

@Entity(tableName = TABLE_NAME)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    val id: Long = 0L,
    val name: String = "",
    val debtDate: Long = Calendar.getInstance().timeInMillis,
    val amount: Int = 0, // centavos
    val tags: String = "",
    val description: String = ""
)

fun RegistrationViewParams.toExpenseEntity(): ExpenseEntity {
    return with(this) {
        ExpenseEntity(
            name = this.name,
            debtDate = this.debtDate.toLong(),
            amount = this.amount.replace(".", "").replace(",", "").toInt(),
            tags = this.tags,
            description = this.description
        )
    }
}

fun ExpenseEntity.toExpense(): Expense {
    return with(this) {
        Expense(
            id = this.id.toString(),
            name = this.name,
            debtDate = this.debtDate.toString(),
            amount = this.amount.toString(),
            tags = this.tags,
            description = this.description
        )
    }
}

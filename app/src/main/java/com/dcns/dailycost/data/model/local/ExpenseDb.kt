package com.dcns.dailycost.data.model.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "expense_table")
data class ExpenseDb (
    @PrimaryKey
    @ColumnInfo(name = "id_expense") val id: Int,
    @ColumnInfo(name = "name_expense") val name: String,
    @ColumnInfo(name = "date_expense") val date: Long,
    @ColumnInfo(name = "amount_expense") val amount: Double,
    @ColumnInfo(name = "payment_expense") val payment: String
): Parcelable
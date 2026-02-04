package com.hisuperaman.wallety.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Long,
    val type: TransactionType,
    val comment: String,
    val paymentType: PaymentType,
    val category: Category,
    val date: Long,
    val createdAt: Long,
    val updatedAt: Long
)
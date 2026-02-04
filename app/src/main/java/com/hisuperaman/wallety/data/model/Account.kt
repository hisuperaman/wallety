package com.hisuperaman.wallety.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val balance: Long,
    val createdAt: Long,
    val updatedAt: Long
)
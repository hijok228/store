package com.example.fefustore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(

    @PrimaryKey
    val id: String,

    val name: String
)
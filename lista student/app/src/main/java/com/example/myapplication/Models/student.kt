package com.example.myapplication.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class student(
        @PrimaryKey(autoGenerate = true)  val id: String,
         val nome: String,
         val curso: String,
         val universidade: String
)


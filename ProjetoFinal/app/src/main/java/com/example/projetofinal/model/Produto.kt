package com.example.projetofinal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produto")
data class Produto(
    @PrimaryKey(autoGenerate = true)
    val id: String,
    val nome: String,
    val preco: Double,
    val descricao: String,
    val url: String
)

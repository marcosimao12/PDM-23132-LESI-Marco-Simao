package com.example.projetofinal.model

data class Carrinho(
    val id: String = "",          // ID do carrinho no Firestore
    val userId: String = "",      // Usuário (dono) do carrinho
    val itens: List<CarrinhoItem> = emptyList()
)


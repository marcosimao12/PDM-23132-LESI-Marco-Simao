package com.example.projetofinal.model

data class Carrinho(
    val id: String = "",                // Firestore doc ID
    val ownerEmail: String = "",        // E-mail do dono
    val authorizedEmails: List<String> = emptyList(),  // Quem pode adicionar itens
    val itens: List<CarrinhoItem> = emptyList()
)



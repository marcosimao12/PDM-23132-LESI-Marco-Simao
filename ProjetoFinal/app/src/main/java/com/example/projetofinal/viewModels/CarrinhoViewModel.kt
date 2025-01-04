package com.example.projetofinal.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.projetofinal.model.CarrinhoItem
import com.example.projetofinal.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class CarrinhoViewModel : ViewModel() {
    private val _carrinho = MutableStateFlow<List<CarrinhoItem>>(emptyList())
    val carrinho: StateFlow<List<CarrinhoItem>> get() = _carrinho

    var donoId: String? = null
        private set

    fun inicializarCarrinho(context: Context) {
        if (donoId == null) {
            val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            donoId = sharedPreferences.getString("DONO_ID", null) ?: UUID.randomUUID().toString()
            sharedPreferences.edit().putString("DONO_ID", donoId).apply()
        }
    }

    fun adicionarAoCarrinho(produto: Produto) {
        _carrinho.update { carrinhoAtual ->
            val existente = carrinhoAtual.find { it.produtoId == produto.id }
            if (existente != null) {
                // Incrementar quantidade do produto já no carrinho
                carrinhoAtual.map {
                    if (it.produtoId == produto.id) {
                        it.copy(quantidade = it.quantidade + 1)
                    } else {
                        it
                    }
                }
            } else {
                // Adicionar novo produto ao carrinho
                carrinhoAtual + CarrinhoItem(produtoId = produto.id.toString(), nome = produto.nome, quantidade = 1)
            }
        }
    }
}

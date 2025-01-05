package com.example.projetofinal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetofinal.data.firebase.FirebaseObj
import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import com.example.projetofinal.model.Produto
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProdutoViewModel : ViewModel() {

    private var produtosListener: ListenerRegistration? = null

    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> get() = _produtos

    fun listenProdutosEmTempoReal() {
        // Cancela listener anterior para evitar duplicações
        produtosListener?.remove()

        // Chama a função genérica
        produtosListener = FirebaseObj.listenToData(
            collection = "produtos",      // Nome da coleção
            documentId = null,           // null => queremos ouvir a coleção inteira
            onDataChanged = { listMap ->
                if (listMap != null) {
                    // Convertemos cada Map em um Produto
                    val listaProdutos = listMap.map { mapToProduto(it) }
                    _produtos.value = listaProdutos
                } else {
                    _produtos.value = emptyList()
                }
            },
            onError = { e ->
                e.printStackTrace()
                // Trate o erro, por exemplo, mandar logs ou exibir mensagem
            }
        )
    }

    private fun mapToProduto(data: Map<String, Any>): Produto {
        // Ajustar conforme seu data class
        val id = data["id"] as? String ?: ""
        val nome = data["nome"] as? String ?: ""
        val preco = data["preco"] as? Double ?: 0.0
        val descricao = data["descricao"] as? String ?: ""

        return Produto(
            id = id,
            nome = nome,
            preco = preco,
            descricao = descricao
        )
    }

    override fun onCleared() {
        super.onCleared()
        // Remove o listener para evitar vazamento de memória
        produtosListener?.remove()
    }
}

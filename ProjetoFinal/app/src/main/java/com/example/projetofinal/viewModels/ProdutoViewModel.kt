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
        produtosListener?.remove()

        produtosListener = FirebaseObj.listenToData(
            collection = "produtos",
            documentId = null,
            onDataChanged = { listMap ->
                if (listMap != null) {
                    viewModelScope.launch {
                        val listaProdutos = listMap.map { mapData ->
                            val produtoBasico = mapToProduto(mapData)
                            // Se 'produtoBasico.url' for só o path, converta:
                            if (produtoBasico.url.isNotBlank()) {
                                val publicLink = FirebaseObj.getImageUrl(produtoBasico.url)
                                if (!publicLink.isNullOrBlank()) {
                                    produtoBasico.copy(url = publicLink)
                                } else {
                                    produtoBasico
                                }
                            } else {
                                produtoBasico
                            }
                        }
                        _produtos.value = listaProdutos
                    }
                } else {
                    _produtos.value = emptyList()
                }
            },
            onError = { e ->
                e.printStackTrace()
            }
        )
    }


    private fun mapToProduto(data: Map<String, Any>): Produto {
        val id = data["id"] as? String ?: ""
        val nome = data["nome"] as? String ?: ""
        val preco = data["preco"] as? Double ?: 0.0
        val descricao = data["descricao"] as? String ?: ""
        val url = data["url"] as? String ?: ""
        return Produto(
            id = id,
            nome = nome,
            preco = preco,
            descricao = descricao,
            url = url
        )
    }

    override fun onCleared() {
        super.onCleared()
        produtosListener?.remove()
    }
}

package com.example.projetofinal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetofinal.data.firebase.FirebaseObj
import com.example.projetofinal.data.repository.CarrinhoRepository
import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarrinhoViewModel : ViewModel() {

    private val repository = CarrinhoRepository()

    private val _carrinhoAtual = MutableStateFlow<Carrinho?>(null)
    val carrinhoAtual: StateFlow<Carrinho?> get() = _carrinhoAtual

    private val _listaCarrinhos = MutableStateFlow<List<Carrinho>>(emptyList())
    private var carrinhoListener: ListenerRegistration? = null
    private var carrinhosListener: ListenerRegistration? = null
    private val _carrinhosAutorizados = MutableStateFlow<List<Carrinho>>(emptyList())
    val carrinhosAutorizados: StateFlow<List<Carrinho>> get() = _carrinhosAutorizados

    fun finalizarCompra(cartId: String) {
        viewModelScope.launch {
            repository.apagarCarrinho(cartId)
            _carrinhoAtual.value = null
        }
    }

    fun carregarCarrinhosAutorizadosDoUser() {
        viewModelScope.launch {
            val todosCarrinhos = repository.getTodosCarrinhos()
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            val carrinhosFiltrados = todosCarrinhos.filter { c ->
                currentUserEmail != null && (
                        c.ownerEmail == currentUserEmail ||
                                c.authorizedEmails.contains(currentUserEmail)
                        )
            }

            _carrinhosAutorizados.value = carrinhosFiltrados
        }
    }

    fun listenCarrinhoEmTempoReal(cartId: String) {
        carrinhoListener?.remove()

        carrinhoListener = FirebaseObj.listenToData(
            collection = "carrinho",
            documentId = cartId,
            onDataChanged = { docs ->
                if (!docs.isNullOrEmpty()) {
                    val data = docs.first()
                    val carrinho = mapToCarrinho(data)
                    _carrinhoAtual.value = carrinho
                } else {
                    _carrinhoAtual.value = null
                }
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    private fun mapToCarrinho(data: Map<String, Any>): Carrinho {
        val id = data["id"] as? String ?: ""
        val ownerEmail = data["ownerEmail"] as? String ?: ""
        val authorizedEmails = data["authorizedEmails"] as? List<String> ?: emptyList()
        val itensList = data["itens"] as? List<Map<String, Any>> ?: emptyList()

        val itens = itensList.map { mapItem ->
            CarrinhoItem(
                produtoId = mapItem["produtoId"] as? String ?: "",
                nome = mapItem["nome"] as? String ?: "",
                preco = (mapItem["preco"] as? Double) ?: 0.0,
                quantidade = (mapItem["quantidade"] as? Long)?.toInt() ?: 1
            )
        }
        return Carrinho(
            id = id,
            ownerEmail = ownerEmail,
            authorizedEmails = authorizedEmails,
            itens = itens
        )
    }

    override fun onCleared() {
        super.onCleared()
        carrinhoListener?.remove()
        carrinhosListener?.remove()
    }

    fun autorizarEmail(cartId: String, novoEmail: String) {
        viewModelScope.launch {
            repository.autorizarOutroEmail(cartId, novoEmail)
            val atualizado = repository.getCarrinhoPorId(cartId)
            _carrinhoAtual.value = atualizado
        }
    }

    fun adicionarItemSeAutorizado(cartId: String, emailQuemAdiciona: String, item: CarrinhoItem) {
        viewModelScope.launch {
            val carrinho = repository.getCarrinhoPorId(cartId)
            if (carrinho != null) {
                val podeAdicionar = (
                        carrinho.ownerEmail == emailQuemAdiciona
                                || carrinho.authorizedEmails.contains(emailQuemAdiciona)
                        )
                if (podeAdicionar) {
                    val sucesso = repository.adicionarItemAoCarrinho(cartId, item)
                    if (sucesso) {
                        _carrinhoAtual.value = repository.getCarrinhoPorId(cartId)
                    }
                } else {
                }
            }
        }
    }

    fun buscarOuCriarCarrinhoPorEmail(ownerEmail: String) {
        viewModelScope.launch {
            val cartId = repository.criarCarrinhoPorEmail(ownerEmail)
            if (!cartId.isNullOrEmpty()) {
                val carrinho = repository.getCarrinhoPorId(cartId)
                _carrinhoAtual.value = carrinho
            }
        }
    }

}

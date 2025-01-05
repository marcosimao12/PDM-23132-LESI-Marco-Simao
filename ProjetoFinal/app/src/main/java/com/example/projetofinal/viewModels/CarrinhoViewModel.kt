package com.example.projetofinal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetofinal.data.repository.CarrinhoRepository
import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarrinhoViewModel : ViewModel() {

    private val repository = CarrinhoRepository()

    private val _carrinhoAtual = MutableStateFlow<Carrinho?>(null)
    val carrinhoAtual: StateFlow<Carrinho?> get() = _carrinhoAtual

    private val _listaCarrinhos = MutableStateFlow<List<Carrinho>>(emptyList())
    val listaCarrinhos: StateFlow<List<Carrinho>> get() = _listaCarrinhos


    // Carregar carrinhos de um e-mail (pode ter 1 ou vários)
    fun carregarCarrinhosPorEmail(email: String) {
        viewModelScope.launch {
            val carrinhos = repository.getCarrinhosPorEmail(email)
            _listaCarrinhos.value = carrinhos
            // Se quiser setar um carrinho como "atual" (ex: o primeiro):
            _carrinhoAtual.value = carrinhos.firstOrNull()
        }
    }

    // Exemplo de método que autoriza outro e-mail a usar o carrinho
    fun autorizarEmail(cartId: String, novoEmail: String) {
        viewModelScope.launch {
            repository.autorizarOutroEmail(cartId, novoEmail)
            // Atualizar local
            val atualizado = repository.getCarrinhoPorId(cartId)
            _carrinhoAtual.value = atualizado
        }
    }


    // Adiciona item se quem está adicionando for dono ou autorizado
    fun adicionarItemSeAutorizado(cartId: String, emailQuemAdiciona: String, item: CarrinhoItem) {
        viewModelScope.launch {
            val carrinho = repository.getCarrinhoPorId(cartId)
            if (carrinho != null) {
                val podeAdicionar = (
                        carrinho.ownerEmail == emailQuemAdiciona ||
                                carrinho.authorizedEmails.contains(emailQuemAdiciona)
                        )
                if (podeAdicionar) {
                    repository.adicionarItemAoCarrinho(cartId, item)
                    val carrinhoAtualizado = repository.getCarrinhoPorId(cartId)
                    _carrinhoAtual.value = carrinhoAtualizado
                } else {
                    // Não autorizado
                    // Pode lançar exceção, ou expor um StateFlow de erro
                }
            }
        }
    }
    fun buscarOuCriarCarrinhoPorEmail(email: String) {
        viewModelScope.launch {
            val cartId = repository.criarCarrinhoPorEmail(email)
            // (ou se quiser só buscar, use getCarrinhosPorEmail)
            if (!cartId.isNullOrEmpty()) {
                val carrinho = repository.getCarrinhoPorId(cartId)
                _carrinhoAtual.value = carrinho
            }
        }
    }

    fun adicionarItemAoCarrinhoPorEmail(ownerEmail: String, item: CarrinhoItem) {
        viewModelScope.launch {
            // 1) Pega carrinhos desse e-mail
            val carrinhos = repository.getCarrinhosPorEmail(ownerEmail)
            val carrinhoExistente = carrinhos.firstOrNull()

            if (carrinhoExistente == null) {
                // Se não existe, cria
                val cartId = repository.criarCarrinhoPorEmail(ownerEmail)
                if (!cartId.isNullOrEmpty()) {
                    repository.adicionarItemAoCarrinho(cartId, item)
                    _carrinhoAtual.value = repository.getCarrinhoPorId(cartId)
                }
            } else {
                // Se já existe, adiciona direto
                repository.adicionarItemAoCarrinho(carrinhoExistente.id, item)
                _carrinhoAtual.value = repository.getCarrinhoPorId(carrinhoExistente.id)
            }
        }
    }

}


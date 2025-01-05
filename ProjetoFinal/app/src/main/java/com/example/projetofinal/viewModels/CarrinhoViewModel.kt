package com.example.projetofinal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetofinal.data.repository.CarrinhoRepository
import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarrinhoViewModel : ViewModel() {

    private val repository = CarrinhoRepository()

    private val _carrinhoAtual = MutableStateFlow<Carrinho?>(null)
    val carrinhoAtual: StateFlow<Carrinho?> get() = _carrinhoAtual

    private val _listaCarrinhos = MutableStateFlow<List<Carrinho>>(emptyList())
    val listaCarrinhos: StateFlow<List<Carrinho>> get() = _listaCarrinhos


    fun carregarCarrinhosPorEmail(email: String) {
        viewModelScope.launch {
            val carrinhos = repository.getCarrinhosPorEmail(email)
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email


            val carrinhosAutorizados = carrinhos.filter { carrinho ->
                currentUserEmail != null && (
                        carrinho.ownerEmail == currentUserEmail
                                || carrinho.authorizedEmails.contains(currentUserEmail)
                        )
            }

            val primeiroCarrinho = carrinhosAutorizados.firstOrNull()

            // Atualiza o estado:
            _listaCarrinhos.value = carrinhosAutorizados
            _carrinhoAtual.value = primeiroCarrinho
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
            // 1) Pega todos os carrinhos do ownerEmail
            val carrinhos = repository.getCarrinhosPorEmail(ownerEmail)
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

            // 2) Filtra só carrinhos onde o user atual é dono ou autorizado
            val carrinhoExistente = carrinhos.firstOrNull { c ->
                currentUserEmail != null && (
                        c.ownerEmail == currentUserEmail ||
                                c.authorizedEmails.contains(currentUserEmail)
                        )
            }

            if (carrinhoExistente == null) {
                // Se não existe carrinho ou o usuário não é autorizado, não faz nada
                // (Opcional: você poderia criar o carrinho se 'currentUserEmail == ownerEmail')
                return@launch
            }

            // 3) Se existe e o user é autorizado, adiciona/incrementa o item no carrinho
            val sucesso = repository.adicionarItemAoCarrinho(carrinhoExistente.id, item)
            if (sucesso) {
                // 4) Atualiza _carrinhoAtual local
                _carrinhoAtual.value = repository.getCarrinhoPorId(carrinhoExistente.id)
            } else {
                // Tratamento de erro (ex.: falha de rede)
            }
        }
    }



}


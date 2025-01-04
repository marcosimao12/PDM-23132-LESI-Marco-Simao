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

    /**
     * Cria um novo carrinho para o userId especificado e atualiza o fluxo carrinhoAtual.
     */
    fun criarCarrinho(userId: String) {
        viewModelScope.launch {
            val cartId = repository.criarCarrinhoParaUsuario(userId)
            if (!cartId.isNullOrEmpty()) {
                // Carrega o carrinho recém-criado
                val carrinho = repository.getCarrinhoPorId(cartId)
                _carrinhoAtual.value = carrinho
            }
        }
    }

    /**
     * Adiciona um item ao carrinho atual (baseado em carrinhoAtual).
     */
    fun adicionarItemAoCarrinho(item: CarrinhoItem) {
        viewModelScope.launch {
            val cartId = _carrinhoAtual.value?.id ?: return@launch
            val sucesso = repository.adicionarItemAoCarrinho(cartId, item)
            if (sucesso) {
                // Atualiza o carrinhoAtual local
                val carrinhoAtualizado = repository.getCarrinhoPorId(cartId)
                _carrinhoAtual.value = carrinhoAtualizado
            }
        }
    }

    /**
     * Carrega um carrinho específico por ID e atualiza o estado.
     */
    fun carregarCarrinhoPorId(cartId: String) {
        viewModelScope.launch {
            val carrinho = repository.getCarrinhoPorId(cartId)
            _carrinhoAtual.value = carrinho
        }
    }

    /**
     * Carrega todos os carrinhos de um usuário e atualiza o _listaCarrinhos.
     */
    fun carregarCarrinhosPorUser(userId: String) {
        viewModelScope.launch {
            val carrinhos = repository.getCarrinhosPorUserId(userId)
            // Se quiser exibir todos, guarde em _listaCarrinhos
            _listaCarrinhos.value = carrinhos
            // Se quiser mostrar no "carrinhoAtual", defina o primeiro carrinho (ou sua própria lógica):
            _carrinhoAtual.value = carrinhos.firstOrNull()
        }
    }

    /**
     * Carrega todos os carrinhos do sistema (caso queira ver o de todos os usuários).
     */
    fun carregarTodosCarrinhos() {
        viewModelScope.launch {
            val carrinhos = repository.getTodosCarrinhos()
            _listaCarrinhos.value = carrinhos
        }
    }
    fun adicionarItemAoMeuCarrinho(userId: String, item: CarrinhoItem) {
        viewModelScope.launch {
            // 1) Verifica se existe carrinho para esse user
            val listaCarrinhos = repository.getCarrinhosPorUserId(userId)

            // Neste exemplo, vamos assumir que cada usuário só tem 1 carrinho.
            val carrinhoExistente = listaCarrinhos.firstOrNull()

            if (carrinhoExistente == null) {
                // Cria um carrinho novo
                val cartId = repository.criarCarrinhoParaUsuario(userId)
                if (!cartId.isNullOrEmpty()) {
                    repository.adicionarItemAoCarrinho(cartId, item)
                    _carrinhoAtual.value = repository.getCarrinhoPorId(cartId)
                }
            } else {
                // Já existe carrinho, então só adicionamos
                repository.adicionarItemAoCarrinho(carrinhoExistente.id, item)
                _carrinhoAtual.value = repository.getCarrinhoPorId(carrinhoExistente.id)
            }
        }
    }
    fun buscarOuCriarCarrinho(userId: String) {
        viewModelScope.launch {
            // 1) Buscar se há carrinhos para esse user
            val listaCarrinhos = repository.getCarrinhosPorUserId(userId)
            val carrinhoExistente = listaCarrinhos.firstOrNull()

            if (carrinhoExistente == null) {
                // 2) Se não existe, criar
                val cartId = repository.criarCarrinhoParaUsuario(userId)
                if (!cartId.isNullOrEmpty()) {
                    // Pega o carrinho recém-criado
                    val novoCarrinho = repository.getCarrinhoPorId(cartId)
                    _carrinhoAtual.value = novoCarrinho
                }
            } else {
                // 3) Se já existe, usar o primeiro carrinho (ou você define a lógica)
                _carrinhoAtual.value = carrinhoExistente
            }
        }
    }


}

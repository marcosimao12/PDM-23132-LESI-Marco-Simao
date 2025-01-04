package com.example.projetofinal.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetofinal.data.firebase.FirebaseObj
import com.example.projetofinal.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProdutoViewModel : ViewModel() {
    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> get() = _produtos


    // Função para buscar todos os produtos do Firestore
    fun buscarProdutos() {
        viewModelScope.launch {
            try {
                val resultado = FirebaseObj.getData("produtos")
                val listaDeProdutos = resultado?.map { dados ->
                    Produto(
                       // id = dados["id"]?.toString()?.toIntOrNull() ?: 0,
                        id = dados["id"]?.toString() ?: "",
                        nome = dados["nome"] as? String ?: "",
                        preco = (dados["preco"] as? Double) ?: 0.0,
                        descricao = dados["descricao"] as? String ?: ""
                    )
                } ?: emptyList()

                // Atualiza o StateFlow com os produtos
                _produtos.update { listaDeProdutos }
            } catch (e: Exception) {
                // Lida com possíveis erros
                e.printStackTrace()
            }
        }
    }
}

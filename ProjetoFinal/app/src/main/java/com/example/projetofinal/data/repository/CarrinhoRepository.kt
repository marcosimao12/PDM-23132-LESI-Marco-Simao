package com.example.projetofinal.data.repository

import android.util.Log
import com.example.projetofinal.data.firebase.FirebaseObj
import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CarrinhoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val carrinhosCollection = db.collection("carrinho")

    /**
     * Cria um carrinho para o usuário (gera um ID no Firestore automaticamente).
     * Retorna o ID do carrinho criado.
     */
    suspend fun criarCarrinhoParaUsuario(userId: String): String? {
        return try {
            val novoCarrinho = Carrinho(
                id = "",       // Deixamos vazio aqui; será gerado ao inserir no Firestore
                userId = userId,
                itens = emptyList()
            )
            val docRef = carrinhosCollection.add(novoCarrinho).await()
            // Atualiza o carrinho com o ID gerado:
            carrinhosCollection.document(docRef.id)
                .update("id", docRef.id)
                .await()
            docRef.id
        } catch (e: Exception) {
            Log.e("CarrinhoRepository", "Erro ao criar carrinho", e)
            null
        }
    }

    /**
     * Adiciona um item ao carrinho especificado por cartId.
     * Se quiser atualizar a quantidade, deve-se ler o carrinho atual e reescrever.
     */
    suspend fun adicionarItemAoCarrinho(cartId: String, item: CarrinhoItem): Boolean {
        return try {
            val snapshot = carrinhosCollection.document(cartId).get().await()
            if (snapshot.exists()) {
                val carrinhoAtual = snapshot.toObject(Carrinho::class.java)
                if (carrinhoAtual != null) {
                    val listaAtualizada = carrinhoAtual.itens.toMutableList()
                    listaAtualizada.add(item)

                    carrinhosCollection.document(cartId)
                        .update("itens", listaAtualizada)
                        .await()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("CarrinhoRepository", "Erro ao adicionar item ao carrinho", e)
            false
        }
    }

    /**
     * Busca um carrinho específico pelo ID.
     */
    suspend fun getCarrinhoPorId(cartId: String): Carrinho? {
        return try {
            val snapshot = carrinhosCollection.document(cartId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(Carrinho::class.java)
            } else null
        } catch (e: Exception) {
            Log.e("CarrinhoRepository", "Erro ao buscar carrinho por ID", e)
            null
        }
    }

    /**
     * Busca todos os carrinhos de um dado usuário.
     */
    suspend fun getCarrinhosPorUserId(userId: String): List<Carrinho> {
        return try {
            val querySnapshot = carrinhosCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            querySnapshot.documents.mapNotNull { it.toObject(Carrinho::class.java) }
        } catch (e: Exception) {
            Log.e("CarrinhoRepository", "Erro ao buscar carrinhos por userId", e)
            emptyList()
        }
    }

    /**
     * Busca todos os carrinhos do sistema (caso queria ver carrinho de outros usuários).
     */
    suspend fun getTodosCarrinhos(): List<Carrinho> {
        return try {
            val querySnapshot = carrinhosCollection.get().await()
            querySnapshot.documents.mapNotNull { it.toObject(Carrinho::class.java) }
        } catch (e: Exception) {
            Log.e("CarrinhoRepository", "Erro ao buscar todos os carrinhos", e)
            emptyList()
        }
    }
}

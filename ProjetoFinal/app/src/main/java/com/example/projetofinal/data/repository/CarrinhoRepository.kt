package com.example.projetofinal.data.repository


import android.util.Log
import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CarrinhoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val carrinhosCollection = db.collection("carrinho")


    suspend fun criarCarrinhoPorEmail(ownerEmail: String): String? {
        return try {
            val snapshot = carrinhosCollection
                .whereEqualTo("ownerEmail", ownerEmail)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                return snapshot.documents.first().id
            }

            val novoCarrinho = Carrinho(
                id = "",
                ownerEmail = ownerEmail,
                authorizedEmails = emptyList(),
                itens = emptyList()
            )
            val docRef = carrinhosCollection.add(novoCarrinho).await()

            // Atualiza o ID
            carrinhosCollection.document(docRef.id)
                .update("id", docRef.id)
                .await()

            docRef.id
        } catch (e: Exception) {
            Log.e("CRIAR_CARRINHO", "Erro ao criar carrinho: ${e.message}", e)
            null
        }
    }

    suspend fun getCarrinhoPorId(cartId: String): Carrinho? {
        return try {
            val doc = carrinhosCollection.document(cartId).get().await()
            if (doc.exists()) {
                doc.toObject(Carrinho::class.java)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun adicionarItemAoCarrinho(cartId: String, item: CarrinhoItem): Boolean {
        return try {
            val snapshot = carrinhosCollection.document(cartId).get().await()
            if (snapshot.exists()) {
                val carrinho = snapshot.toObject(Carrinho::class.java)
                if (carrinho != null) {
                    val itensAtualizados = carrinho.itens.toMutableList()
                    val indiceExistente = itensAtualizados.indexOfFirst {
                        it.produtoId == item.produtoId
                    }

                    if (indiceExistente >= 0) {
                        val itemExistente = itensAtualizados[indiceExistente]
                        val novaQuantidade = itemExistente.quantidade + item.quantidade
                        val precoUnitario = itemExistente.preco / itemExistente.quantidade

                        val itemAtualizado = itemExistente.copy(
                            quantidade = novaQuantidade,
                            preco = precoUnitario * novaQuantidade
                        )
                        itensAtualizados[indiceExistente] = itemAtualizado
                    } else {
                        itensAtualizados.add(item)
                    }

                    carrinhosCollection.document(cartId)
                        .update("itens", itensAtualizados)
                        .await()

                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun autorizarOutroEmail(cartId: String, novoEmail: String): Boolean {
        return try {
            db.collection("carrinho")
                .document(cartId)
                .update("authorizedEmails", FieldValue.arrayUnion(novoEmail))
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getTodosCarrinhos(): List<Carrinho> {
        return try {
            val snapshot = db.collection("carrinho").get().await()
            snapshot.documents.mapNotNull { it.toObject(Carrinho::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun apagarCarrinho(cartId: String): Boolean {
        return try {
            carrinhosCollection.document(cartId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



}

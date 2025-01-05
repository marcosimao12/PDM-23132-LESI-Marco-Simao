package com.example.projetofinal.data.repository


import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CarrinhoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val carrinhosCollection = db.collection("carrinho")

    // Cria um carrinho pelo e-mail do dono, caso não exista
    suspend fun criarCarrinhoPorEmail(ownerEmail: String): String? {
        return try {
            // Verifica se já existe (opcional, pode ser feito no ViewModel)
            val snapshot = carrinhosCollection
                .whereEqualTo("ownerEmail", ownerEmail)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                // Já existe pelo menos um carrinho para esse e-mail. Retorna o primeiro encontrado
                return snapshot.documents.first().id
            }

            // Se não existe, cria um novo
            val novoCarrinho = Carrinho(
                id = "",                 // Gerado automaticamente
                ownerEmail = ownerEmail,
                authorizedEmails = emptyList(),
                itens = emptyList()
            )
            val docRef = carrinhosCollection.add(novoCarrinho).await()
            // Atualiza o "id"
            carrinhosCollection.document(docRef.id)
                .update("id", docRef.id)
                .await()

            docRef.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Retorna todos os carrinhos cujo ownerEmail = email
    suspend fun getCarrinhosPorEmail(email: String): List<Carrinho> {
        return try {
            val snapshot = carrinhosCollection
                .whereEqualTo("ownerEmail", email)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Carrinho::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
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

    // Adiciona item ao carrinho (sem verificar autorização aqui)
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

}

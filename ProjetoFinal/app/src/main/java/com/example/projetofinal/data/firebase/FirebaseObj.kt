package com.example.projetofinal.data.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.example.projetofinal.model.Carrinho
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object FirebaseObj {

    suspend fun getData(
        colecao: String,
        documentoId: String? = null,
        campoFiltro: String? = null,
        valorFiltro: Any? = null
    ): List<Map<String, Any>>? {
        return try {
            val firestore = FirebaseFirestore.getInstance()

            if (documentoId != null) {
                val snapshot = firestore.collection(colecao).document(documentoId).get().await()
                if (snapshot.exists()) {
                    val dados = snapshot.data ?: emptyMap()
                    listOf(dados + ("id" to snapshot.id))
                } else {
                    Log.w("Firestore", "Documento não encontrado")
                    null
                }
            } else {
                // Obter documentos com ou sem filtro
                val referenciaColecao = firestore.collection(colecao)
                val consulta: Query = if (campoFiltro != null && valorFiltro != null) {
                    referenciaColecao.whereEqualTo(campoFiltro, valorFiltro)
                } else {
                    referenciaColecao
                }

                val snapshot = consulta.get().await()
                snapshot.documents.mapNotNull { doc ->
                    val dados = doc.data ?: return@mapNotNull null
                    dados + ("id" to doc.id)
                }
            }
        } catch (e: Exception) {
            Log.w("Firestore", "Erro ao buscar dados", e)
            null
        }
    }


    fun listenToData(
        collection: String,
        documentId: String? = null,
        onDataChanged: (List<Map<String, Any>>?) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        val firestore = FirebaseFirestore.getInstance()

        return if (documentId != null) {
            // Listener para um único documento
            firestore.collection(collection).document(documentId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed for document.", e)
                        onError(e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val data = snapshot.data ?: emptyMap()
                        onDataChanged(listOf(data + ("id" to snapshot.id))) // Retorna o documento como uma lista
                    } else {
                        Log.d(TAG, "Document data: null")
                        onDataChanged(null) // Documento não encontrado
                    }
                }
        } else {
            // Listener para todos os documentos de uma coleção
            firestore.collection(collection)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed for collection.", e)
                        onError(e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val documents = snapshot.documents.mapNotNull { doc ->
                            val data = doc.data ?: return@mapNotNull null
                            Log.d(TAG, "Documento - id: ${doc.id} - ${doc.data}")
                            data + ("id" to doc.id) // Adiciona o id a cada documento
                        }
                        onDataChanged(documents) // Retorna todos os documentos
                    } else {
                        Log.d(TAG, "Collection data: null")
                        onDataChanged(null) // Nenhum dado encontrado
                    }
                }
        }
    }
}


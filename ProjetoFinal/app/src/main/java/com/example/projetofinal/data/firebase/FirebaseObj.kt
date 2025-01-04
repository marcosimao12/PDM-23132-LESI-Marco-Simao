package com.example.projetofinal.data.firebase

import android.util.Log
import com.example.projetofinal.model.Carrinho
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object FirebaseObj {
  private val db = FirebaseFirestore.getInstance()

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
}


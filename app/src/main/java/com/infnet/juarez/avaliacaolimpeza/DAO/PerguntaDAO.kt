package com.infnet.juarez.avaliacaolimpeza.DAO

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.infnet.juarez.avaliacaolimpeza.modelo.Pergunta

class PerguntaDAO {
    private val collection = "perguntas_collection"
    val db = Firebase.firestore

    fun inserir(pergunta: Pergunta): Pergunta {
        if (pergunta.id == null) {
            val ref: DocumentReference = db.collection(collection).document()
            pergunta.id = ref.id
            ref.set(pergunta).addOnSuccessListener {
            }.addOnFailureListener { }
        }
        return pergunta
    }

    fun alterar(pergunta: Pergunta) {
        val ref: DocumentReference = db.collection(collection).document(pergunta.id.toString())
        ref.set(pergunta).addOnSuccessListener() {
        }.addOnFailureListener { }
    }

    fun excluir(id: String): Boolean {
        var retorno: Boolean = false
        val ref: DocumentReference = db.collection(collection).document(id)
        ref.delete()
            .addOnSuccessListener { retorno = true }
            .addOnFailureListener { retorno = false }
        return retorno
    }

    fun obter(id: String): Task<DocumentSnapshot>{
        return db.collection(collection).document(id).get()
    }

    fun listar(): Task<QuerySnapshot> {
        return db.collection("perguntas_collection").get()
    }

}
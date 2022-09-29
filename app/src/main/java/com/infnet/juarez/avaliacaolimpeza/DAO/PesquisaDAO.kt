package com.infnet.juarez.avaliacaolimpeza.DAO

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.infnet.juarez.avaliacaolimpeza.modelo.Pesquisa
import com.infnet.juarez.avaliacaolimpeza.modelo.Usuario

class PesquisaDAO {
    private val collection = "pesquisas_collection"
    val db = Firebase.firestore

    fun inserir(pesquisa: Pesquisa): Pesquisa {
        if (pesquisa.id == null) {
            val ref: DocumentReference = db.collection(collection).document()
            pesquisa.id = ref.id
            ref.set(pesquisa).addOnSuccessListener {
            }.addOnFailureListener {
            }
        }
        return pesquisa
    }

    fun alterar(pesquisa: Pesquisa): Pesquisa {
        val ref: DocumentReference = db.collection(collection).document(pesquisa.id.toString())
        ref.set(pesquisa).addOnSuccessListener() {
        }.addOnFailureListener {
        }
        return pesquisa
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

    fun listar(usuario: Usuario): Task<QuerySnapshot> {
        return db.collection(collection)
            .whereEqualTo("user.id", usuario.id).get()
    }

}
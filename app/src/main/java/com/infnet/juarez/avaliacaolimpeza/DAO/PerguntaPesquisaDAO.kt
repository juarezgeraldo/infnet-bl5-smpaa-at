package com.infnet.juarez.avaliacaolimpeza.DAO

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.infnet.juarez.avaliacaolimpeza.modelo.Estabelecimento
import com.infnet.juarez.avaliacaolimpeza.modelo.PerguntaPesquisa

class PerguntaPesquisaDAO {
    private val collection = "perguntas_pesquisa_collection"
    val db = Firebase.firestore

    fun inserir(perguntaPesquisa: PerguntaPesquisa): PerguntaPesquisa {
        val ref: DocumentReference = db.collection(collection).document()
        perguntaPesquisa.id = ref.id
        ref.set(perguntaPesquisa).addOnSuccessListener {
        }.addOnFailureListener { }
        return perguntaPesquisa
    }

    fun alterar(perguntaPesquisa: PerguntaPesquisa): PerguntaPesquisa {
        val ref: DocumentReference = db.collection(collection).document(perguntaPesquisa.id.toString())
        ref.set(perguntaPesquisa).addOnSuccessListener {
        }.addOnFailureListener { }
        return perguntaPesquisa
    }

    fun listar(idPesquisa: String?, idEstabelecimento: String?): Task<QuerySnapshot> {
        return db.collection(collection)
            .whereEqualTo("pesquisa.id", idPesquisa)
            .whereEqualTo("pesquisa.estabelecimento.id", idEstabelecimento)
            .get()
    }

    fun listarEstatistica(): Task<QuerySnapshot> {
        return db.collection(collection)
            .orderBy("pesquisa.estabelecimento.bairro")
//            .orderBy("perguntaResposta.pergunta")
            .get()
    }

}
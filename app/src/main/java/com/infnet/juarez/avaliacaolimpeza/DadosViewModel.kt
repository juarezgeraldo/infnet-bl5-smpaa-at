package com.infnet.juarez.avaliacaolimpeza

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.model.MutableDocument
import com.infnet.juarez.avaliacaolimpeza.modelo.Estabelecimento
import com.infnet.juarez.avaliacaolimpeza.modelo.Pesquisa
import com.infnet.juarez.avaliacaolimpeza.modelo.Usuario

class DadosViewModel : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: MutableLiveData<FirebaseUser?> = _user

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: MutableLiveData<Usuario?> = _usuario

    private val _estabelecimento = MutableLiveData<Estabelecimento?>()
    val estabelecimento: MutableLiveData<Estabelecimento?> = _estabelecimento

    private val _pesquisa = MutableLiveData<Pesquisa?>()
    val pesquisa: MutableLiveData<Pesquisa?> = _pesquisa


    fun registraUsusario(usuario: Usuario ){
        _usuario.value = usuario
    }
    fun recuperaUsusario(): Usuario? {
        return usuario.value
    }

    fun registraEstabelecimento(estabelecimento: Estabelecimento ){
        _estabelecimento.value = estabelecimento
    }
    fun recuperaEstabelecimento(): Estabelecimento? {
        return estabelecimento.value
    }

    fun registraPesquisa(pesquisa: Pesquisa ){
        _pesquisa.value = pesquisa
    }
    fun recuperaPesquisa(): Pesquisa? {
        return pesquisa.value
    }

}
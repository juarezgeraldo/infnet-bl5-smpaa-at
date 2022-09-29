package com.infnet.juarez.avaliacaolimpeza

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.infnet.juarez.avaliacaolimpeza.modelo.Anotacao
import com.infnet.juarez.avaliacaolimpeza.modelo.Estabelecimento
import com.infnet.juarez.avaliacaolimpeza.modelo.Usuario

class DadosViewModel : ViewModel() {

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: MutableLiveData<Usuario?> = _usuario

    fun registraUsusario(usuario: Usuario ){
        _usuario.value = usuario
    }
    fun recuperaUsusario(): Usuario? {
        return usuario.value
    }

    private val _anotacao = MutableLiveData<Anotacao?>()
    val anotacao: MutableLiveData<Anotacao?> = _anotacao

    fun registraAnotacao(anotacao: Anotacao ){
        _anotacao.value = anotacao
    }
    fun recuperaAnotacao(): Anotacao? {
        return anotacao.value
    }
}
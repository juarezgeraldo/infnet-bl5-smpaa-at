package com.infnet.juarez.avaliacaolimpeza.modelo

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat

data class Usuario (
    var id: String? = null,
    var email: String? = null,
    var dataCriacao: String? = null,
    var dataUltimoAcesso: String? = null,
)
//
//    constructor(
//        id: String,
//        email: String,
//        dataCriacao: String,
//        dataUltimoAcesso: String,
//    ){
//        this.id = id
//        this.email = id
//        this.dataCriacao = dataCriacao
//        this.dataUltimoAcesso = dataUltimoAcesso
//    }
//
//    constructor()
//}

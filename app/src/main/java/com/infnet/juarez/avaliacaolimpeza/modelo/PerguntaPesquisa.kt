package com.infnet.juarez.avaliacaolimpeza.modelo

data class PerguntaPesquisa(
    var id: String? = null,
    var pesquisa: Pesquisa? = null,
    var perguntaResposta: ArrayList<PerguntaResposta> = ArrayList()
)

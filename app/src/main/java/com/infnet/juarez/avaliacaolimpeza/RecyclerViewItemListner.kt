package com.infnet.juarez.avaliacaolimpeza

import android.view.View
import com.infnet.juarez.avaliacaolimpeza.modelo.PerguntaResposta

interface RecyclerViewItemListner {
    fun recyclerViewBotaoAlterarClicked(view: View, pos: Int)
    fun recyclerViewBotaoExcluirClicked(view: View, pos: Int): Boolean
    fun recyclerViewBotaoEditaClicked(view: View, pos: Int)

    fun recyclerViewRadioButton(view: View, perguntaResposta: PerguntaResposta, resposta: Boolean)
}


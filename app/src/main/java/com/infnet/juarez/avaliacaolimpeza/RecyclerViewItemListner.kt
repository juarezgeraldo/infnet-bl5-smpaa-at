package com.infnet.juarez.avaliacaolimpeza

import android.view.View

interface RecyclerViewItemListner {
    fun recyclerViewBotaoAlterarClicked(view: View, pos: Int)
    fun recyclerViewBotaoExcluirClicked(view: View, pos: Int): Boolean
    fun recyclerViewBotaoEditaClicked(view: View, pos: Int)
}


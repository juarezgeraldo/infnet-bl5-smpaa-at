package com.infnet.juarez.avaliacaolimpeza

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.infnet.juarez.avaliacaolimpeza.modelo.Estabelecimento

class ListaEstabelecimentoAdapter(private val listaEstabelecimentos: ArrayList<Estabelecimento>) :
    RecyclerView.Adapter<ListaEstabelecimentoAdapter.ViewHolder>() {

    //    var listaEstabelecimentos = ArrayList<Estabelecimento>()
//    set(value){
//        field = value
//        this.notifyDataSetChanged()
//    }
    lateinit var itemListner: RecyclerViewItemListner

    fun setRecyclerViewItemListner(listner: RecyclerViewItemListner) {
        itemListner = listner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.lista_estabelecimentos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listaEstabelecimentos[position], itemListner, position)
    }

    override fun getItemCount(): Int {
        return listaEstabelecimentos.size
    }

    //Classe interna = relação Tdo - Parte
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(estabelecimento: Estabelecimento, itemListner: RecyclerViewItemListner, position: Int) {
            val txtListaEstabelecimento = itemView.findViewById<TextView>(R.id.txtEstabelecimento)
            txtListaEstabelecimento.setText(estabelecimento.nome)
            val btnExcluirEstabelecimento = itemView.findViewById<ImageButton>(R.id.btnExcluirEstabelecimento)
            val btnAlterarEstabelecimento = itemView.findViewById<ImageButton>(R.id.btnAlterarEstabelecimento)

            btnExcluirEstabelecimento.setOnClickListener() {
                itemListner.recyclerViewBotaoExcluirClicked(it, position)
            }
            btnAlterarEstabelecimento.setOnClickListener() {
                itemListner.recyclerViewBotaoAlterarClicked(it, position)
            }
        }
    }
}

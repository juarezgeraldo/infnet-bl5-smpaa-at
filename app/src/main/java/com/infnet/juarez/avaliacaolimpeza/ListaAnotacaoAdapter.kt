package com.infnet.juarez.avaliacaolimpeza

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.infnet.juarez.avaliacaolimpeza.modelo.Anotacao

class ListaAnotacaoAdapter(private val listaArquivo: ArrayList<String>) :
    RecyclerView.Adapter<ListaAnotacaoAdapter.ViewHolder>() {

    //    var listaAnotacaos = ArrayList<Anotacao>()
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
            LayoutInflater.from(parent.context).inflate(R.layout.lista_anotacao, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listaArquivo[position], itemListner, position)
    }

    override fun getItemCount(): Int {
        return listaArquivo.size
    }

    //Classe interna = relação Tdo - Parte
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(arquivo: String, itemListner: RecyclerViewItemListner, position: Int) {

            val txtLstData = itemView.findViewById<TextView>(R.id.txtLstData)
            val txtLstTitulo = itemView.findViewById<TextView>(R.id.txtLstTitulo)
            txtLstData.setText(arquivo)
            txtLstTitulo.setText(arquivo)

        }
    }
}

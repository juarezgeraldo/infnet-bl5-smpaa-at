package com.infnet.juarez.avaliacaolimpeza

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.infnet.juarez.avaliacaolimpeza.modelo.PerguntaResposta

class ListaRespostasAdapter(private val listaPerguntasRespostas: ArrayList<PerguntaResposta>) :
    RecyclerView.Adapter<ListaRespostasAdapter.ViewHolder>() {

    lateinit var itemListner: RecyclerViewItemListner

    fun setRecyclerViewItemListner(listner: RecyclerViewItemListner) {
        itemListner = listner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lista_perguntas_respostas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listaPerguntasRespostas[position], itemListner, position)
    }

    override fun getItemCount(): Int {
        return listaPerguntasRespostas.size
    }

    //Classe interna = relação Tdo - Parte
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(
            perguntaResposta: PerguntaResposta,
            itemListner: RecyclerViewItemListner,
            position: Int
        ) {
            val txtPerguntaResposta = itemView.findViewById<TextView>(R.id.txtPerguntasRespostas)
            txtPerguntaResposta.setText(perguntaResposta.pergunta)
            val groupRadioButton = itemView.findViewById<RadioGroup>(R.id.radioGroup)
            val rdbSim = itemView.findViewById<RadioButton>(R.id.rdbSim)
            val rdbNao = itemView.findViewById<RadioButton>(R.id.rdbNao)

            if (perguntaResposta.resposta == true) {
                rdbSim.isChecked = true
                rdbNao.isChecked = false
            } else {
                if (perguntaResposta.resposta == false) {
                    rdbSim.isChecked = false
                    rdbNao.isChecked = true
                } else {
                    rdbSim.isChecked = false
                    rdbNao.isChecked = false
                }
            }

            perguntaResposta.pergunta = txtPerguntaResposta.text.toString()
            perguntaResposta.resposta = rdbSim.isChecked

            groupRadioButton.setOnCheckedChangeListener { radioGroup, i ->
                return@setOnCheckedChangeListener
            }

           rdbSim.setOnClickListener() {
                itemListner.recyclerViewRadioButton(it, perguntaResposta, true)
            }
            rdbNao.setOnClickListener() {
                itemListner.recyclerViewRadioButton(it, perguntaResposta, false)
            }
        }
    }
}

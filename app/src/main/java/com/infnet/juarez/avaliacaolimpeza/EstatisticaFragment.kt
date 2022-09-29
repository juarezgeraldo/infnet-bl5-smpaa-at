package com.infnet.juarez.avaliacaolimpeza

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infnet.juarez.avaliacaolimpeza.DAO.PerguntaPesquisaDAO
import com.infnet.juarez.avaliacaolimpeza.modelo.PerguntaPesquisa

class EstatisticaFragment : Fragment() {
    private val perguntaPesquisaDAO = PerguntaPesquisaDAO()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = inflater.inflate(R.layout.fragment_estatistica, container, false)

        val lstEstatistica = fragmentBinding.findViewById<ListView>(R.id.lstEstatistica)
        val fabEstatisticaLogout = fragmentBinding.findViewById<FloatingActionButton>(R.id.fabEstatisticaLogout)

        fabEstatisticaLogout.setOnClickListener(){
            findNavController().navigate(R.id.action_estatisticaFragment_to_menuFragment)
        }


        atualizaRelatorioEstatistico()
        return fragmentBinding
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun atualizaRelatorioEstatistico() {
        val obj = perguntaPesquisaDAO.listarEstatistica()
        obj.addOnSuccessListener {
            var relBairro: String = ""
            var mediaSim: Int = 0
            var mediaNao: Int = 0
            var contador: Int = 0
            val linhaRel = ArrayList<String>()

            for (objeto in it) {
                val perguntaPesquisa = objeto.toObject(PerguntaPesquisa::class.java)
                if (relBairro != perguntaPesquisa.pesquisa?.estabelecimento?.bairro.toString() &&
                    relBairro != ""
                ) {
                    linhaRel.add("bairro: ${relBairro} - Sim: ${mediaSim} - Não: ${mediaNao}")

                    mediaSim = 0
                    mediaNao = 0
                    contador = 0
                }
                relBairro = perguntaPesquisa.pesquisa?.estabelecimento?.bairro.toString()
                for (perg in perguntaPesquisa.perguntaResposta) {
                    if (perg.resposta == true) {
                        mediaSim += 1
                    } else {
                        mediaNao += 1
                    }
                    contador += 1
                }
            }
            linhaRel.add("bairro: ${relBairro} - Sim: ${mediaSim} - Não: ${mediaNao}")

            val lstEstatistica = this.requireActivity().findViewById<ListView>(R.id.lstEstatistica)
            val adapterEstatistica = ArrayAdapter<String>(
                this.requireActivity(),
                android.R.layout.simple_list_item_1,
                linhaRel
            )
            lstEstatistica.adapter = adapterEstatistica

        }.addOnFailureListener()
        {
            val a = "erro"
        }
    }
}
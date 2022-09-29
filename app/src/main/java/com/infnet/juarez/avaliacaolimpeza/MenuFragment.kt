package com.infnet.juarez.avaliacaolimpeza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class MenuFragment : Fragment() {

    private lateinit var btnPesquisa: Button
    private lateinit var btnCadEstabelecimento: Button
    private lateinit var btnRelEstatistico: Button
    private lateinit var btnLogoff: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = inflater.inflate(R.layout.fragment_menu, container, false)

        btnPesquisa = fragmentBinding.findViewById(R.id.btnPesquisa)
        btnRelEstatistico = fragmentBinding.findViewById(R.id.btnRelEstatistico)
        btnCadEstabelecimento = fragmentBinding.findViewById(R.id.btnCadEstabelecimento)
        btnLogoff = fragmentBinding.findViewById(R.id.btnLogoff)

        btnPesquisa.setOnClickListener() {
            findNavController().navigate(R.id.action_menuFragment_to_pesquisaFragment)
        }
        btnCadEstabelecimento.setOnClickListener(){
            findNavController().navigate(R.id.action_menuFragment_to_estabelecimentoFragment)
        }
        btnRelEstatistico.setOnClickListener(){
            findNavController().navigate(R.id.action_menuFragment_to_estatisticaFragment)
        }
        btnLogoff.setOnClickListener(){
            findNavController().navigate(R.id.action_menuFragment_to_loginFragment)
        }

        return fragmentBinding
    }
}
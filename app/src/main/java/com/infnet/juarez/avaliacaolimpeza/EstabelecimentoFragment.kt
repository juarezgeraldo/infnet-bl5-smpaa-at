package com.infnet.juarez.avaliacaolimpeza

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infnet.juarez.avaliacaolimpeza.DAO.EstabelecimentoDAO
import com.infnet.juarez.avaliacaolimpeza.modelo.Estabelecimento
import com.infnet.juarez.avaliacaolimpeza.modelo.PerguntaResposta

class EstabelecimentoFragment : Fragment(), RecyclerViewItemListner {
    private val estabelecimentoDAO = EstabelecimentoDAO()
    private var idEstabelecimentos: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = inflater.inflate(R.layout.fragment_estabelecimento, container, false)

        val edtTxtBairro = fragmentBinding.findViewById<TextView>(R.id.edtTxtBairro)
        val edtTxtEstabelecimento =
            fragmentBinding.findViewById<EditText>(R.id.edtTxtEstabelecimento)
        val edtCep = fragmentBinding.findViewById<EditText>(R.id.edtCep)
        val txtId = fragmentBinding.findViewById<TextView>(R.id.txtId)
        val btnSalvar = fragmentBinding.findViewById<Button>(R.id.btnSalvar)
        val fabEstabelecimentoLogout = fragmentBinding.findViewById<FloatingActionButton>(R.id.fabEstabelecimentoLogout)

        fabEstabelecimentoLogout.setOnClickListener(){
            findNavController().navigate(R.id.action_estabelecimentoFragment_to_menuFragment)
        }

        btnSalvar.setOnClickListener {
            if (validaCamposEstabelecimento(
                    edtTxtEstabelecimento.text.toString(),
                    edtTxtBairro.text.toString(),
                    edtCep.text.toString()
                )
            ) {
                if (txtId.text.toString().isEmpty()) {
                    val estabelecimento = Estabelecimento(
                        null,
                        edtTxtEstabelecimento.text.toString(),
                        edtCep.text.toString(),
                        edtTxtBairro.text.toString()
                    )
                    atualizaEstabelecimento(estabelecimento, "incluir")
                } else {
                    val estabelecimento = Estabelecimento(
                        txtId.text.toString(),
                        edtTxtEstabelecimento.text.toString(),
                        edtCep.text.toString(),
                        edtTxtBairro.text.toString()
                    )
                    this.atualizaEstabelecimento(estabelecimento, "alterar")
                }
                txtId.setText(null)
                edtTxtEstabelecimento.setText(null)
                edtCep.setText(null)
                edtTxtBairro.setText(null)

                this.atualizaListaEstabelecimentos()
            }
        }
        atualizaListaEstabelecimentos()

        return fragmentBinding
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        atualizaListaEstabelecimentos()
    }

    override fun onStart() {
        super.onStart()
//        atualizaListaEstabelecimentos()
    }

    private fun validaCamposEstabelecimento(nome: String, bairro: String, cep: String): Boolean {
        var mensagem: String = ""
        if (nome.isEmpty()) {
            mensagem = "Nome do estabelecimento deve ser informado"
        } else {
            if (bairro.isEmpty()) {
                mensagem = "Bairro deve ser informado"
            } else {
                if (cep.isEmpty()) {
                    mensagem = "CEP deve ser informado e válido"
                }
            }
        }
        if (mensagem.isEmpty()) {
            return true
        } else {
            Toast.makeText(this.requireActivity(), mensagem, Toast.LENGTH_LONG).show()
            return false
        }
    }

    private fun atualizaListaEstabelecimentos() {
        val obj = estabelecimentoDAO.listar()
        val estabelecimentos: ArrayList<Estabelecimento> = ArrayList()
        idEstabelecimentos = ArrayList()
        obj.addOnSuccessListener {
            for (objeto in it) {
                val estabelecimento = objeto.toObject(Estabelecimento::class.java)
                if (!estabelecimento.nome.isNullOrBlank()) {
                    estabelecimentos.add(estabelecimento)
                    idEstabelecimentos.add(estabelecimento.id!!)
                }
            }
            val lstEstabelecimentos =
                this.requireActivity().findViewById<RecyclerView>(R.id.lstEstabelecimentos)
            lstEstabelecimentos.layoutManager = LinearLayoutManager(this.requireActivity())
            val adapter = ListaEstabelecimentoAdapter(estabelecimentos)
            adapter.setRecyclerViewItemListner(this)
            lstEstabelecimentos.adapter = adapter
        }.addOnFailureListener {
            val a = "erro"
        }
    }

    private fun atualizaEstabelecimento(estabelecimento: Estabelecimento, operacao: String) {
        when (operacao) {
            "incluir" -> {
                estabelecimentoDAO.inserir(estabelecimento)
                Toast.makeText(
                    this.requireActivity(),
                    "Inclusão realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
            "alterar" -> {
                estabelecimentoDAO.alterar(estabelecimento)
                Toast.makeText(
                    this.requireActivity(),
                    "Alteração realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
            "excluir" -> {
                estabelecimentoDAO.excluir(estabelecimento.id!!)
                Toast.makeText(
                    this.requireActivity(),
                    "Exclusão realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        atualizaListaEstabelecimentos()
    }

    override fun recyclerViewBotaoAlterarClicked(view: View, pos: Int) {
        var estabelecimento: Estabelecimento = Estabelecimento()
        val obj = estabelecimentoDAO.obter(idEstabelecimentos[pos])
        obj.addOnSuccessListener {
            estabelecimento = it.toObject(Estabelecimento::class.java)!!

            val txtId = this.requireActivity().findViewById<TextView>(R.id.txtId)
            val edtTxtEstabelecimento =
                this.requireActivity().findViewById<EditText>(R.id.edtTxtEstabelecimento)
            val edtTxtBairro = this.requireActivity().findViewById<EditText>(R.id.edtTxtBairro)
            val edtCep = this.requireActivity().findViewById<EditText>(R.id.edtCep)

            txtId.setText(estabelecimento.id)
            edtTxtEstabelecimento.setText(estabelecimento.nome)
            edtTxtBairro.setText(estabelecimento.bairro)
            edtCep.setText(estabelecimento.cep)
        }.addOnFailureListener {
        }
    }

    override fun recyclerViewBotaoExcluirClicked(view: View, pos: Int): Boolean {
        var estabelecimento: Estabelecimento = Estabelecimento()
        val obj = estabelecimentoDAO.obter(idEstabelecimentos[pos])
        obj.addOnSuccessListener {
            estabelecimento = it.toObject(Estabelecimento::class.java)!!
            if (!estabelecimento.nome.isNullOrBlank()) {
                atualizaEstabelecimento(estabelecimento, "excluir")
            }
        }.addOnFailureListener {
        }
        return true
    }

    override fun recyclerViewBotaoEditaClicked(view: View, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun recyclerViewRadioButton(
        view: View,
        perguntaResposta: PerguntaResposta,
        resposta: Boolean
    ) {
        TODO("Not yet implemented")
    }

}
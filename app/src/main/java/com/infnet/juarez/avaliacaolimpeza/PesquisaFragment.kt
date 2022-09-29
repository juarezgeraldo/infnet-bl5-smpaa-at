package com.infnet.juarez.avaliacaolimpeza

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infnet.juarez.avaliacaolimpeza.DAO.EstabelecimentoDAO
import com.infnet.juarez.avaliacaolimpeza.DAO.PesquisaDAO
import com.infnet.juarez.avaliacaolimpeza.modelo.Estabelecimento
import com.infnet.juarez.avaliacaolimpeza.modelo.PerguntaResposta
import com.infnet.juarez.avaliacaolimpeza.modelo.Pesquisa
import com.infnet.juarez.avaliacaolimpeza.modelo.Usuario


class PesquisaFragment : Fragment(), RecyclerViewItemListner {

    private var usuario: Usuario = Usuario()
    private var pesquisa: Pesquisa = Pesquisa()
    private var estabelecimento: Estabelecimento = Estabelecimento()
    private val pesquisaDAO = PesquisaDAO()
    private val estabelecimentoDAO = EstabelecimentoDAO()

    private val sharedViewModel: DadosViewModel by activityViewModels()

    private var idPesquisas: ArrayList<String> = ArrayList()
    private var idEstabelecimentos: ArrayList<String> = ArrayList()

    private var listaIdEstabelecimentos: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        atualizaListaEstabelecimentos()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = inflater.inflate(R.layout.fragment_pesquisa, container, false)

        usuario = sharedViewModel.recuperaUsusario()!!

        if (usuario == null) {
            findNavController().navigate(R.id.action_pesquisaFragment_to_menuFragment)
        }

        val txtId = fragmentBinding.findViewById<TextView>(R.id.txtId)
        val txtUserPesquisa = fragmentBinding.findViewById<TextView>(R.id.txtIdUserPesquisa)
        val edtNomePesquisa = fragmentBinding.findViewById<EditText>(R.id.edtNomePesquisa)
        val spnEstabelecimento = fragmentBinding.findViewById<Spinner>(R.id.spnEstabelecimento)
        val btnSalvar = fragmentBinding.findViewById<Button>(R.id.btnSalvar)
        val fabPesquisaLogout = fragmentBinding.findViewById<FloatingActionButton>(R.id.fabPesquisaLogout)

        txtUserPesquisa.setText(usuario.email)

        fabPesquisaLogout.setOnClickListener(){
            findNavController().navigate(R.id.action_pesquisaFragment_to_menuFragment)
        }
        btnSalvar.setOnClickListener {
            if (edtNomePesquisa.text.toString().isEmpty()) {
                Toast.makeText(
                    this.requireActivity(),
                    "Informe um nome para a pesquisa.",
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                if (spnEstabelecimento.selectedItemPosition == 0) {
                    Toast.makeText(
                        this.requireActivity(),
                        "Informe qual estabelecimento.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    val obj =
                        estabelecimentoDAO.obter(listaIdEstabelecimentos[spnEstabelecimento.selectedItemPosition])
                    obj.addOnSuccessListener {
                        estabelecimento = it.toObject(estabelecimento::class.java)!!

                        if (txtId.text.toString().isEmpty()) {
                            val pesquisa = Pesquisa(
                                null,
                                usuario,
                                estabelecimento,
                                edtNomePesquisa.text.toString(),
                            )
                            atualizaPesquisa(pesquisa, "incluir")
                        } else {
                            val pesquisa = Pesquisa(
                                txtId.text.toString(),
                                usuario,
                                estabelecimento,
                                edtNomePesquisa.text.toString(),
                            )
                            atualizaPesquisa(pesquisa, "alterar")
                        }
                        txtId.setText(null)
                        edtNomePesquisa.setText(null)
                        spnEstabelecimento.setSelection(0)
                    }.addOnFailureListener {
                    }
                    atualizaListaPesquisas()
                }
            }
        }
        atualizaListaPesquisas()
        return fragmentBinding
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun atualizaListaPesquisas() {
        val obj = pesquisaDAO.listar(usuario)
        val pesquisas: ArrayList<Pesquisa> = ArrayList()
        idPesquisas = ArrayList()
        idEstabelecimentos = ArrayList()
        obj.addOnSuccessListener {
            for (objeto in it) {
                val pesquisa = objeto.toObject(Pesquisa::class.java)
                pesquisas.add(pesquisa)
                idPesquisas.add(pesquisa.id!!)
                idEstabelecimentos.add(pesquisa.estabelecimento?.id!!)
            }
            val lstPesquisas = this.requireActivity().findViewById<RecyclerView>(R.id.lstPesquisas)
            lstPesquisas.layoutManager = LinearLayoutManager(this.requireActivity())
            val adapter = ListaPesquisaAdapter(pesquisas)
            adapter.setRecyclerViewItemListner(this)
            lstPesquisas.adapter = adapter
        }.addOnFailureListener {
            val a = "erro"
        }

    }

    private fun atualizaListaEstabelecimentos() {
        val objEstabelecimento = estabelecimentoDAO.listar()
        val nomesEstabelecimento = ArrayList<String>()
        var posSpin: Int = 0
        objEstabelecimento.addOnSuccessListener {
            for (objeto in it) {
                val estabelecimento = objeto.toObject(Estabelecimento::class.java)
                nomesEstabelecimento.add(estabelecimento.nome!!)
                listaIdEstabelecimentos.add(estabelecimento.id!!)
                posSpin += 1
            }
            val spnEstabelecimento =
                this.requireActivity().findViewById<Spinner>(R.id.spnEstabelecimento)
            val adapterEstabelecimento =
                ArrayAdapter(
                    this.requireActivity(),
                    android.R.layout.simple_spinner_item,
                    nomesEstabelecimento
                )
            spnEstabelecimento.adapter = adapterEstabelecimento
        }.addOnFailureListener {
            val a = "erro"
        }
    }

    private fun atualizaPesquisa(pesquisa: Pesquisa, operacao: String) {
        when (operacao) {
            "incluir" -> {
                pesquisaDAO.inserir(pesquisa)
                Toast.makeText(
                    this.requireActivity(),
                    "Inclusão realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
            "alterar" -> {
                pesquisaDAO.alterar(pesquisa)
                Toast.makeText(
                    this.requireActivity(),
                    "Alteração realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
            "excluir" -> {
                pesquisaDAO.excluir(pesquisa.id!!)
                Toast.makeText(
                    this.requireActivity(),
                    "Exclusão realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        atualizaListaPesquisas()
    }

    override fun recyclerViewBotaoAlterarClicked(view: View, pos: Int) {
        val obj = pesquisaDAO.obter(idPesquisas[pos])
        obj.addOnSuccessListener {
            pesquisa = it.toObject(Pesquisa::class.java)!!
            val txtId = this.requireActivity().findViewById<TextView>(R.id.txtId)
            val txtUserPesquisa =
                this.requireActivity().findViewById<TextView>(R.id.txtIdUserPesquisa)
            val edtNomePesquisa =
                this.requireActivity().findViewById<EditText>(R.id.edtNomePesquisa)
            val spnEstabelecimento =
                this.requireActivity().findViewById<Spinner>(R.id.spnEstabelecimento)

            txtId.setText(pesquisa.id)
            txtUserPesquisa.setText(pesquisa.user?.email!!)
            edtNomePesquisa.setText(pesquisa.nomePesquisa)
            spnEstabelecimento.setSelection(1)
        }.addOnFailureListener{
        }
    }

    override fun recyclerViewBotaoExcluirClicked(view: View, pos: Int): Boolean {
        val obj = pesquisaDAO.obter(idPesquisas[pos])
        obj.addOnSuccessListener {
            pesquisa = it.toObject(Pesquisa::class.java)!!

            atualizaPesquisa(pesquisa, "excluir")
        }.addOnFailureListener {
        }
        return true
    }

    override fun recyclerViewBotaoEditaClicked(view: View, pos: Int) {
        val objPesquisa = pesquisaDAO.obter(idPesquisas[pos])
        val objEstabelecimento = estabelecimentoDAO.obter(idEstabelecimentos[pos])
        objPesquisa.addOnSuccessListener {
            pesquisa = it.toObject(Pesquisa::class.java)!!
            objEstabelecimento.addOnSuccessListener {
                estabelecimento = it.toObject(Estabelecimento::class.java)!!
                sharedViewModel.registraEstabelecimento(estabelecimento)
                sharedViewModel.registraPesquisa(pesquisa)
                findNavController().navigate(R.id.action_pesquisaFragment_to_pesquisaPerguntaFragment)
            }.addOnFailureListener {
            }
        }.addOnFailureListener {
        }
    }

    override fun recyclerViewRadioButton(
        view: View,
        perguntaResposta: PerguntaResposta,
        resposta: Boolean
    ) {
    }

}


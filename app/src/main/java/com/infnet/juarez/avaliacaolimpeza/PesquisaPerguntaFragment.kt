package com.infnet.juarez.avaliacaolimpeza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.infnet.juarez.avaliacaolimpeza.DAO.EstabelecimentoDAO
import com.infnet.juarez.avaliacaolimpeza.DAO.PerguntaDAO
import com.infnet.juarez.avaliacaolimpeza.DAO.PerguntaPesquisaDAO
import com.infnet.juarez.avaliacaolimpeza.DAO.PesquisaDAO
import com.infnet.juarez.avaliacaolimpeza.modelo.*

class PesquisaPerguntaFragment : Fragment(), RecyclerViewItemListner {

    private var usuario: Usuario = Usuario()
    private var pesquisa: Pesquisa = Pesquisa()
    private var estabelecimento: Estabelecimento = Estabelecimento()
    private val pesquisaDAO = PesquisaDAO()
    private val perguntaDAO = PerguntaDAO()
    private val perguntaPesquisaDAO = PerguntaPesquisaDAO()
    private val estabelecimentoDAO = EstabelecimentoDAO()
    private var inserir: Boolean = false
    private var idPerguntaPesquisa: String? = null

    private val sharedViewModel: DadosViewModel by activityViewModels()
    private var perguntaPesquisa : PerguntaPesquisa = PerguntaPesquisa()

    private var perguntasRespostas: ArrayList<PerguntaResposta> = ArrayList()
    private lateinit var listaPerguntaPesquisa: ArrayList<PerguntaPesquisa>
    private var listaIdEstabelecimentos: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        usuario = sharedViewModel.recuperaUsusario()!!
        estabelecimento = sharedViewModel.recuperaEstabelecimento()!!
        pesquisa = sharedViewModel.recuperaPesquisa()!!

        if (usuario == null) {
            findNavController().navigate(R.id.action_pesquisaPerguntaFragment_to_menuFragment)
        }

        atualizaListaPerguntas()
        val fragmentBinding =
            inflater.inflate(R.layout.fragment_pesquisa_pergunta, container, false)

        val txtId = fragmentBinding.findViewById<TextView>(R.id.txtId)
        val txtUsuario = fragmentBinding.findViewById<TextView>(R.id.txtUsuario)
        val txtNomeEstabelecimento =
            fragmentBinding.findViewById<TextView>(R.id.txtNomeEstabelecimento)
        val txtNomePesquisaPergunta =
            fragmentBinding.findViewById<TextView>(R.id.txtNomePesquisaPergunta)
        val btnSalvar = fragmentBinding.findViewById<Button>(R.id.btnSalvarPesquisa)
        val fabPesquisaPerguntaLogout = fragmentBinding.findViewById<FloatingActionButton>(R.id.fabPesquisaPerguntaLogout)

        fabPesquisaPerguntaLogout.setOnClickListener(){
            findNavController().navigate(R.id.action_pesquisaPerguntaFragment_to_menuFragment)
        }


        txtUsuario.setText(usuario.email)
        txtNomeEstabelecimento.setText(estabelecimento.nome)
        txtNomePesquisaPergunta.setText(pesquisa.nomePesquisa)

        btnSalvar.setOnClickListener {

            perguntaPesquisa.pesquisa = pesquisa
            perguntaPesquisa.perguntaResposta = perguntasRespostas

            if (inserir) {
                perguntaPesquisa.id = null
                perguntaPesquisaDAO.inserir(perguntaPesquisa)
            }else{
                perguntaPesquisaDAO.alterar(perguntaPesquisa)
            }

            Toast.makeText(
                this.requireActivity(),
                "Pesquisa alterada com sucesso.",
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigate(R.id.action_pesquisaPerguntaFragment_to_pesquisaFragment)

        }
        return fragmentBinding
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun atualizaListaPerguntas() {
        val objPerguntasPesquisa = perguntaPesquisaDAO.listar(pesquisa.id, estabelecimento.id)
        perguntasRespostas = ArrayList()
        objPerguntasPesquisa.addOnSuccessListener {
            if (!it.isEmpty) {
                inserir = false
                val perguntaResposta = PerguntaResposta()
                for (objeto in it) {
                    val perguntaPesquisa = objeto.toObject(PerguntaPesquisa::class.java)
                    idPerguntaPesquisa = perguntaPesquisa.id
                    for (perg in perguntaPesquisa.perguntaResposta) {
                        perguntaResposta.id = perg.id
                        perguntaResposta.pergunta = perg.pergunta
                        perguntaResposta.resposta = perg.resposta
                        perguntasRespostas.add(perguntaResposta)
                    }
                }
                val lstPerguntasRespostas =
                    this.requireActivity().findViewById<RecyclerView>(R.id.lstPerguntasRespostas)
                lstPerguntasRespostas.layoutManager = LinearLayoutManager(this.requireActivity())
                val adapter = ListaRespostasAdapter(perguntasRespostas)
                adapter.setRecyclerViewItemListner(this)
                lstPerguntasRespostas.adapter = adapter
            } else {
                inserir = true
                val objPerguntas = perguntaDAO.listar()
                perguntasRespostas = ArrayList()
                objPerguntas.addOnSuccessListener {
                    for (objeto in it) {
                        val pergunta = objeto.toObject(Pergunta::class.java)
                        val perguntaResposta = PerguntaResposta()
                        perguntaResposta.id = pergunta.id
                        perguntaResposta.pergunta = pergunta.pergunta
                        perguntaResposta.resposta = null
                        perguntasRespostas.add(perguntaResposta)
                    }
                    val lstPerguntasRespostas =
                        this.requireActivity()
                            .findViewById<RecyclerView>(R.id.lstPerguntasRespostas)
                    lstPerguntasRespostas.layoutManager =
                        LinearLayoutManager(this.requireActivity())
                    val adapter = ListaRespostasAdapter(perguntasRespostas)
                    adapter.setRecyclerViewItemListner(this)
                    lstPerguntasRespostas.adapter = adapter
                }.addOnFailureListener {
                    val a = "erro"
                }
            }
        }.addOnFailureListener {
            val a = "erro"
        }
    }

    override fun recyclerViewBotaoAlterarClicked(view: View, pos: Int) {
    }

    override fun recyclerViewBotaoExcluirClicked(view: View, pos: Int): Boolean {
        return false
    }

    override fun recyclerViewBotaoEditaClicked(view: View, pos: Int) {
    }

    override fun recyclerViewRadioButton(
        view: View,
        perguntaResposta: PerguntaResposta,
        resposta: Boolean
    ) {
//        perguntaResposta.resposta = resposta

    }
}
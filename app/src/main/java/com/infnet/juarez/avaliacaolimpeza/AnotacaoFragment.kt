package com.infnet.juarez.avaliacaolimpeza

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infnet.juarez.avaliacaolimpeza.modelo.Anotacao
import com.infnet.juarez.avaliacaolimpeza.modelo.Usuario
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class AnotacaoFragment : Fragment(), RecyclerViewItemListner, LocationListener{
    private var usuario: Usuario = Usuario()
    private var anotacao: Anotacao = Anotacao()
    private var idanotacaos: ArrayList<String> = ArrayList()
    private val sharedViewModel: DadosViewModel by activityViewModels()

    private var isInclusao: Boolean = true

    val EXTERNAL_STORAGE_PERMISSION_CODE = 100
    val COARSE_REQUEST = 12345
    val FINE_REQUEST = 54321

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = inflater.inflate(R.layout.fragment_anotacao, container, false)

        val txtUsuario = fragmentBinding.findViewById<TextView>(R.id.txtUsuario)
        val edtData = fragmentBinding.findViewById<EditText>(R.id.edtData)
        val edtLatitude = fragmentBinding.findViewById<EditText>(R.id.edtLatitude)
        val edtLongitude = fragmentBinding.findViewById<EditText>(R.id.edtLongitude)
        val edtTitulo = fragmentBinding.findViewById<EditText>(R.id.edtTitulo)
        val edtTexto = fragmentBinding.findViewById<EditText>(R.id.edtTexto)
        val imgFoto = fragmentBinding.findViewById<ImageView>(R.id.imgFoto)
        val btnFoto = fragmentBinding.findViewById<Button>(R.id.btnFoto)
        val btnSalvar = fragmentBinding.findViewById<Button>(R.id.btnSalvar)
        val fabAnotacaoLogout = fragmentBinding.findViewById<FloatingActionButton>(R.id.fabAnotacaoLogout)

        edtData.isEnabled = false
        edtLatitude.isEnabled = false
        edtLongitude.isEnabled = false

        fabAnotacaoLogout.setOnClickListener(){
            findNavController().navigate(R.id.action_anotacaoFragment_to_loginFragment)
        }

        anotacao.data = dateFormat.format(Date())

        getLocation("GPS")


        edtData.setText(dateFormat.format(Date()))



        usuario = sharedViewModel.recuperaUsusario()!!

        txtUsuario.setText(usuario.email)

        btnSalvar.setOnClickListener {
            if (validaCamposAnotacao(
                    edtTitulo.text.toString(),
                    edtTexto.text.toString()
                )
            ) {
                anotacao.titulo = edtTitulo.text.toString()
                anotacao.texto = edtTexto.text.toString()
                if (isInclusao) {
                    atualizaanotacao(anotacao, "incluir")
                } else {
                    this.atualizaanotacao(anotacao, "alterar")
                }
                edtTexto.setText(null)
                edtTitulo.setText(null)
                edtData.setText(dateFormat.format(Date()))

                this.atualizaListaanotacaos()
            }
        }
        atualizaListaanotacaos()

        return fragmentBinding
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        atualizaListaanotacaos()
    }

    override fun onStart() {
        super.onStart()
//        atualizaListaanotacaos()
    }

    private fun getLocation(tipo: String) {
        var location: Location? = null
        val locationManager =
            getActivity()?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        val isServiceEnable =
            locationManager.isProviderEnabled(if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER)
        if (isServiceEnable) {
            Log.i("DR4", "Indo pela Rede")
            if (checkSelfPermission(this.requireActivity(),
                    if (tipo == "NET") android.Manifest.permission.ACCESS_COARSE_LOCATION else Manifest.permission.ACCESS_FINE_LOCATION
                ).equals( PackageManager.PERMISSION_GRANTED)
            ) {
                locationManager.requestLocationUpdates(
                    if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER,
                    2000L,
                    0f,
                    this
                )
                location =
                    locationManager.getLastKnownLocation(if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER)
                anotacao.latitude = location?.latitude.toString()
                anotacao.longitude = location?.longitude.toString()
            } else {
                requestPermissions(
                    arrayOf(if (tipo == "NET") android.Manifest.permission.ACCESS_COARSE_LOCATION else Manifest.permission.ACCESS_FINE_LOCATION),
                    if (tipo == "NET") COARSE_REQUEST else FINE_REQUEST
                )
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((requestCode == COARSE_REQUEST || requestCode == FINE_REQUEST) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.getLocation("GPS")
        }
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isExternalStorageWritable()) {
                    if (isExternalStorageWritable()) {
//                        acessaLocalizacao()
                    }
                }
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun gravaRegistro(anotacao: Anotacao) {
        if (getActivity()?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                EXTERNAL_STORAGE_PERMISSION_CODE
            )
        } else {
            if (isExternalStorageWritable()) {
                val dataAtual = dateFormat.format(Date())

                val feleName = anotacao.titulo + "(" + dataAtual + ")" + ".txt"
                val builder = StringBuilder()
                builder.append("Data: ").append(anotacao.data).append("\n")
                    .append("Longitude: ").append(anotacao.longitude).append("\n")
                    .append("Latitude: ").append(anotacao.latitude).append("\n")
                    .append("Titulo: ").append(anotacao.latitude).append("\n")
                    .append("Texto: ").append(anotacao.latitude).append("\n")

                val registroTxt = builder.toString()

                val fileOutputStream = getActivity()?.openFileOutput(feleName, AppCompatActivity.MODE_APPEND)
                fileOutputStream?.write(registroTxt.toByteArray())
                fileOutputStream?.close()
            }
        }
    }


    private fun validaCamposAnotacao(titulo: String, texto: String): Boolean {
        var mensagem: String = ""
        if (titulo.isEmpty()) {
            mensagem = "Título do anotação deve ser informado"
        } else {
            if (texto.isEmpty()) {
                mensagem = "Texto da anotação deve ser informado"
            }
        }
        if (mensagem.isEmpty()) {
            return true
        } else {
            Toast.makeText(this.requireActivity(), mensagem, Toast.LENGTH_LONG).show()
            return false
        }
    }

    private fun atualizaListaanotacaos() {
//        val anotacaos: ArrayList<Anotacao> = ArrayList()
//
//        obj.addOnSuccessListener {
//            for (objeto in it) {
//                val anotacao = objeto.toObject(anotacao::class.java)
//                if (!anotacao.nome.isNullOrBlank()) {
//                    anotacaos.add(anotacao)
//                    idanotacaos.add(anotacao.id!!)
//                }
//            }
//            val lstanotacaos =
//                this.requireActivity().findViewById<RecyclerView>(R.id.lstanotacaos)
//            lstanotacaos.layoutManager = LinearLayoutManager(this.requireActivity())
//            val adapter = ListaanotacaoAdapter(anotacaos)
//            adapter.setRecyclerViewItemListner(this)
//            lstanotacaos.adapter = adapter
//        }.addOnFailureListener {
//            val a = "erro"
//        }
    }

    private fun atualizaanotacao(anotacao: Anotacao, operacao: String) {
        when (operacao) {
            "incluir" -> {
                gravaRegistro(anotacao)
                Toast.makeText(
                    this.requireActivity(),
                    "Inclusão realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
            "alterar" -> {
                gravaRegistro(anotacao)
                Toast.makeText(
                    this.requireActivity(),
                    "Alteração realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
            "excluir" -> {
                gravaRegistro(Anotacao())
                Toast.makeText(
                    this.requireActivity(),
                    "Exclusão realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        atualizaListaanotacaos()
    }

    override fun recyclerViewBotaoAlterarClicked(view: View, pos: Int) {
//        var anotacao: anotacao = anotacao()
//        val obj = anotacaoDAO.obter(idanotacaos[pos])
//        obj.addOnSuccessListener {
//            anotacao = it.toObject(anotacao::class.java)!!
//
//            val txtId = this.requireActivity().findViewById<TextView>(R.id.txtId)
//            val edtTxtanotacao =
//                this.requireActivity().findViewById<EditText>(R.id.edtData)
//            val edtTxtBairro = this.requireActivity().findViewById<EditText>(R.id.edtTitulo)
//            val edtCep = this.requireActivity().findViewById<EditText>(R.id.edtTexto)
//
//            txtId.setText(anotacao.id)
//            edtTxtanotacao.setText(anotacao.nome)
//            edtTxtBairro.setText(anotacao.bairro)
//            edtCep.setText(anotacao.cep)
//        }.addOnFailureListener {
//        }
    }

    override fun recyclerViewBotaoExcluirClicked(view: View, pos: Int): Boolean {
//        var anotacao: anotacao = anotacao()
//        val obj = anotacaoDAO.obter(idanotacaos[pos])
//        obj.addOnSuccessListener {
//            anotacao = it.toObject(anotacao::class.java)!!
//            if (!anotacao.nome.isNullOrBlank()) {
//                atualizaanotacao(anotacao, "excluir")
//            }
//        }.addOnFailureListener {
//        }
        return true
    }

    override fun recyclerViewBotaoEditaClicked(view: View, pos: Int) {
        TODO("Not yet implemented")
    }

}
package com.infnet.juarez.avaliacaolimpeza

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infnet.juarez.avaliacaolimpeza.modelo.Anotacao
import com.infnet.juarez.avaliacaolimpeza.modelo.Foto
import com.infnet.juarez.avaliacaolimpeza.modelo.Usuario
import java.util.*


class AnotacaoFragment : Fragment(), RecyclerViewItemListner, LocationListener {
    private var usuario: Usuario = Usuario()
    private var anotacao: Anotacao = Anotacao()
    private var foto: Foto = Foto()
    private val sharedViewModel: DadosViewModel by activityViewModels()

    lateinit var mAdView : AdView

    private var isInclusao: Boolean = true

    val EXTERNAL_STORAGE_PERMISSION_CODE = 200
    val COARSE_REQUEST = 12345
    val FINE_REQUEST = 54321
    val CAMERA_PERMISSION_CODE = 100
    val CAMERA_REQUEST = 1888

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = inflater.inflate(R.layout.fragment_anotacao, container, false)

        mAdView = AdView(requireActivity())

        val txtUsuario = fragmentBinding.findViewById<TextView>(R.id.txtUsuario)
        val edtData = fragmentBinding.findViewById<EditText>(R.id.edtData)
        val edtLatitude = fragmentBinding.findViewById<EditText>(R.id.edtLatitude)
        val edtLongitude = fragmentBinding.findViewById<EditText>(R.id.edtLongitude)
        val edtTitulo = fragmentBinding.findViewById<EditText>(R.id.edtTitulo)
        val edtTexto = fragmentBinding.findViewById<EditText>(R.id.edtTexto)
        val imgFoto = fragmentBinding.findViewById<ImageView>(R.id.imgFoto)
        val btnFoto = fragmentBinding.findViewById<Button>(R.id.btnFoto)
        val btnSalvar = fragmentBinding.findViewById<Button>(R.id.btnSalvar)
        val fabAnotacaoLogout =
            fragmentBinding.findViewById<FloatingActionButton>(R.id.fabAnotacaoLogout)

        edtData.isEnabled = false
        edtLatitude.isEnabled = false
        edtLongitude.isEnabled = false

        anotacao.data = dateFormat.format(Date())
        edtData.setText(dateFormat.format(Date()))

        var location = getLocation("GPS")
        if (location == null) {
            location = getLocation("NET")
        }
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
                    atualizaAnotacao("incluir")
                } else {
                    this.atualizaAnotacao("alterar")
                }
                edtTexto.setText(null)
                edtTitulo.setText(null)
                edtData.setText(dateFormat.format(Date()))
                this.atualizaListaanotacaos()
            }
        }

        btnFoto.setOnClickListener() {
            if (this.getActivity()
                    ?.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                this.requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        }

        fabAnotacaoLogout.setOnClickListener() {
            findNavController().navigate(R.id.action_anotacaoFragment_to_loginFragment)
        }

        atualizaListaanotacaos()

        mAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }

        return fragmentBinding
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val edtLatitude = requireActivity().findViewById<EditText>(R.id.edtLatitude)
        val edtLongitude = requireActivity().findViewById<EditText>(R.id.edtLongitude)


        MobileAds.initialize(requireActivity()) {}
        val adView = AdView(requireActivity())
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        mAdView = requireActivity().findViewById<AdView>(R.id.adView)
        mAdView.loadAd(adRequest)

        edtLatitude.setText(anotacao.latitude)
        edtLongitude.setText(anotacao.longitude)

//        atualizaListaanotacaos()
    }

    override fun onStart() {
        super.onStart()
//        atualizaListaanotacaos()
    }

    private fun getLocation(tipo: String): Location? {
        var location: Location? = null
        val locationManager =
            getActivity()?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        val isServiceEnable =
            locationManager.isProviderEnabled(if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER)
        if (isServiceEnable) {
            if (checkSelfPermission(
                    this.requireActivity(),
                    if (tipo == "NET") android.Manifest.permission.ACCESS_COARSE_LOCATION else Manifest.permission.ACCESS_FINE_LOCATION
                ).equals(PackageManager.PERMISSION_GRANTED)
            ) {
                locationManager.requestLocationUpdates(
                    if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER,
                    2000L,
                    0f,
                    this
                )
                location =
                    locationManager.getLastKnownLocation(if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER)
                anotacao.longitude = location?.longitude.toString()
                anotacao.latitude = location?.latitude.toString()
            } else {
                requestPermissions(
                    arrayOf(if (tipo == "NET") android.Manifest.permission.ACCESS_COARSE_LOCATION else Manifest.permission.ACCESS_FINE_LOCATION),
                    if (tipo == "NET") COARSE_REQUEST else FINE_REQUEST
                )
            }
        }
        return location
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
        if (requestCode == COARSE_REQUEST || requestCode == FINE_REQUEST) {
//            acessaLocalizacao()
        }
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            val imagem = (data?.extras!!["data"] as Bitmap?)!!
            val imgFoto = requireActivity().findViewById<ImageView>(R.id.imgFoto)
            imgFoto.setImageBitmap(imagem)

            val fileName = anotacao.titulo + "(" + anotacao.data + ")" + ".fig"



        }
    }
    private fun gravaRegistroFoto(foto: Foto) {
        val fileName = anotacao.titulo + "(" + anotacao.data + ")" + ".fig"
        val fileOutputStream =
            getActivity()?.openFileOutput(fileName, AppCompatActivity.MODE_APPEND)
        fileOutputStream?.write("".toByteArray())
        fileOutputStream?.close()
    }


    private fun gravaRegistro(anotacao: Anotacao) {
        val fileName = anotacao.titulo + "(" + anotacao.data + ")" + ".txt"
        val builder = StringBuilder()
        builder.append("Data: ").append(anotacao.data).append("\n")
            .append("Longitude: ").append(anotacao.longitude).append("\n")
            .append("Latitude: ").append(anotacao.latitude).append("\n")
            .append("Titulo: ").append(anotacao.titulo).append("\n")
            .append("Texto: ").append(anotacao.texto).append("\n")

        val registroTxt = builder.toString()

        val fileOutputStream =
            getActivity()?.openFileOutput(fileName, AppCompatActivity.MODE_APPEND)
        fileOutputStream?.write(registroTxt.toByteArray())
        fileOutputStream?.close()
    }

    private fun validaCamposAnotacao(titulo: String, texto: String): Boolean {
        var mensagem: String = ""
        if (titulo.isEmpty()) {
            mensagem = "T??tulo do anota????o deve ser informado"
        } else {
            if (texto.isEmpty()) {
                mensagem = "Texto da anota????o deve ser informado"
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



//        val file: File
//        val minhaLista: ArrayList<String>
//
//        minhaLista = ArrayList()
//
//        val root_sd = Environment.getExternalStorageDirectory().toString()
//        file = File("$root_sd/pasta")
//        val list: Array<File> = file.listFiles() as Array<File>
//
//        for (i in list.indices) {
//            minhaLista.add(list[i].getName())
//        }
//        val lstanotacaos =
//            this.requireActivity().findViewById<RecyclerView>(R.id.lstanotacaos)
//        lstanotacaos.layoutManager = LinearLayoutManager(this.requireActivity())
//        val adapter = ListaAnotacaoAdapter(minhaLista)
//        adapter.setRecyclerViewItemListner(this)
//        lstanotacaos.adapter = adapter
    }

    private fun atualizaAnotacao(operacao: String) {
        when (operacao) {
            "incluir" -> {
                gravaRegistro(anotacao)
                gravaRegistroFoto(foto)
                Toast.makeText(
                    this.requireActivity(),
                    "Inclus??o realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
            "alterar" -> {
                gravaRegistro(anotacao)
                Toast.makeText(
                    this.requireActivity(),
                    "Altera????o realizada com sucesso.",
                    Toast.LENGTH_LONG
                ).show()
            }
            "excluir" -> {
                gravaRegistro(Anotacao())
                Toast.makeText(
                    this.requireActivity(),
                    "Exclus??o realizada com sucesso.",
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
//                atualizaAnotacao(anotacao, "excluir")
//            }
//        }.addOnFailureListener {
//        }
        return true
    }

    override fun recyclerViewBotaoEditaClicked(view: View, pos: Int) {
        TODO("Not yet implemented")
    }

}
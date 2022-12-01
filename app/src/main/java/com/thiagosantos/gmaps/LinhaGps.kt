package com.thiagosantos.gmaps

import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thiagosantos.gmaps.adapter.MarkerLinha
import com.thiagosantos.gmaps.helper.BitmapHelper
import com.thiagosantos.gmaps.helper.LinhasHelper
import com.thiagosantos.gmaps.model.LinhaRastreio
import com.thiagosantos.gmaps.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LinhaGps : AppCompatActivity() {

    var linha: LinhasHelper? = null
    private val listLinhas = MutableLiveData<LinhaRastreio>()
    private val TAG = "DEBUG"
    private lateinit var mMap: GoogleMap
    var mParada = 0
    var mLinha = 0


    // time count down for 30 seconds,
    // with 1 second as countDown interval
    val timerCounter = object : CountDownTimer(30000, 1000) {

        // Callback function, fired on regular interval
        override fun onTick(millisUntilFinished: Long) {
            println("seconds remaining: " + millisUntilFinished / 1000)
        }

        // Callback function, fired
        // when the time is up
        override fun onFinish() {
            mMap.clear()
            println("done!")
            //getLinhasIntent()
            println(mParada)
            println(mLinha)
            // loadMap()
            start()

           refreshCountDownPrevisao()


        }


    }


    override fun onBackPressed() {
        super.onBackPressed()
        timerCounter.cancel()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linha_gps)


        if (checkForInternet(this)) {
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()

            getLinhasIntent()


        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show()
        }

    }

    private fun refreshCountDownPrevisao(){

        if (checkForInternet(this)) {
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()

            getPrevisao(mParada, mLinha)



        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show()
        }
    }


    private fun getPrevisao(p1: Int, p2: Int) {


        try {

            lifecycleScope.launch {

                val response = ApiService.getRatreiaLinha(p1, p2)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        listLinhas.postValue(body)
                        Log.d(
                            ContentValues.TAG,
                            "Lista de Linhas desta LINHA+========+++++>>>>>>>>-> Teste saída: ${body}"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Hummm!, você está sem sinal de Internet.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "API fora do ar: " + e.message)
        }

    }


    private fun getLinhasIntent() {


        linha = intent.getParcelableExtra("linha")

        Log.d(
            "imprimi objeto",
            "Imprimi objeto LinhasHelper preenchido ---------------->>>>>${linha}"
        )


        val letreiro = findViewById<TextView>(R.id.marqueeText)
        letreiro.text =
            linha?.letreiro + " " + (linha?.destino) + " | " + (linha?.origem) + " --> " + (linha?.destino)
        letreiro.isSelected = true;

        val codigoParada = linha?.codigoParada
        val codigoLinha = linha?.codigoLinha
        if (codigoParada != null) {
            mParada = codigoParada
        }
        if (codigoLinha != null) {
            mLinha = codigoLinha
        }

        val tvCodigoLInha = findViewById<TextView>(R.id.tv_codigoLinha)
        val tvOrigem = findViewById<TextView>(R.id.tv_origem)
        val tvDestino = findViewById<TextView>(R.id.tv_destino)

        tvCodigoLInha.text = codigoLinha.toString()
        tvOrigem.text = linha?.origem
        tvDestino.text = linha?.destino



        lifecycleScope.launch(Dispatchers.IO) {

            if (codigoParada != null) {
                if (codigoLinha != null) {
                    println("getPrevisao")
                    getPrevisao(codigoParada, codigoLinha)
                    //getParadas()
                }
            }
        }


        //refresh button


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (codigoParada != null) {
                if (codigoLinha != null) {
//


                    if (checkForInternet(this)) {
                        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()

                        mMap.clear()
                        getPrevisao(codigoParada, codigoLinha)





                    } else {
                        Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }




        loadMap()

        timerCounter.start()
    }


    private fun loadMap() {


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->

            googleMap.setInfoWindowAdapter(MarkerLinha(this))
            addMarkersParadas(googleMap)


            // getAddress(LatLng(-23.516592, -46.698575))

            googleMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()

                listLinhas.observe(this) {

                    bounds.include(
                        LatLng(
                            it.parada.relacaoLinhas[0].relacaoVeiculos[0].latitude,
                            it.parada.relacaoLinhas[0].relacaoVeiculos[0].longitude
                        )
                    )
                    bounds.include(LatLng(it.parada.latitudeParada, it.parada.longitudeParada))
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds.build(),
                            100
                        )
                    )
                }

            }


        }

    }


    private fun addMarkersParadas(googleMap: GoogleMap) {
        mMap = googleMap

        listLinhas.observe(this) { ponto ->

            //add marker da Parada
            val markerParada = mMap.addMarker(
                MarkerOptions()
                    .title(ponto.parada.codigoParada.toString())
                    .snippet(ponto.parada.nomeParada)
                    .position(LatLng(ponto.parada.latitudeParada, ponto.parada.longitudeParada))
                    .icon(
                        BitmapHelper.vectorToBitmap(
                            this,
                            R.drawable.ic_bus_stop_icon,
                            ContextCompat.getColor(this, R.color.paradaColor)
                        )
                    )
            )


            ponto.parada.relacaoLinhas.forEach { linhas ->
                linhas.relacaoVeiculos.forEach { v ->

                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .title(v.prefixoVeiculo.toString())
                            .snippet(v.horarioPrevisto)
                            .position(LatLng(v.latitude, v.longitude))
                            .icon(
                                BitmapHelper.vectorToBitmap(
                                    this,
                                    R.drawable.ic_baseline_directions_bus_24,
                                    ContextCompat.getColor(this, R.color.onibusColor)
                                )
                            )
                    )


                    if (marker != null) {
                        marker.tag = v
                    }


                }

            }


        }

    }


    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


}

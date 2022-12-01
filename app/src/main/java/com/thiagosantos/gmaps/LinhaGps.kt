package com.thiagosantos.gmaps

import android.content.ContentValues
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
import com.thiagosantos.gmaps.helper.CheckInternet
import com.thiagosantos.gmaps.helper.LinhasHelper
import com.thiagosantos.gmaps.model.LinhaRastreio
import com.thiagosantos.gmaps.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LinhaGps : AppCompatActivity() {


    private val checkInternet = CheckInternet()
    var linha: LinhasHelper? = null
    private val listLinhas = MutableLiveData<LinhaRastreio>()
    private val TAG = "DEBUG"
    private lateinit var mMap: GoogleMap
    var mParada = 0
    var mLinha = 0


    val timerCounter = object : CountDownTimer(30000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            println("seconds remaining: " + millisUntilFinished / 1000)
        }

        override fun onFinish() {
            mMap.clear()
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


        if (checkInternet.checkForInternet(this)) {

            getLinhasIntent()

        } else {
            Toast.makeText(this, "Hummm!, você está sem sinal de Internet.", Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun refreshCountDownPrevisao() {

        if (checkInternet.checkForInternet(this)) {

            getPrevisao(mParada, mLinha)


        } else {
            Toast.makeText(this, "Hummm!, você está sem sinal de Internet.", Toast.LENGTH_LONG)
                .show()
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
                }
            }
        }

        //refresh button

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (codigoParada != null) {
                if (codigoLinha != null) {


                    if (checkInternet.checkForInternet(this)) {

                        mMap.clear()
                        getPrevisao(codigoParada, codigoLinha)


                    } else {
                        Toast.makeText(
                            this,
                            "Hummm!, você está sem sinal de Internet.",
                            Toast.LENGTH_LONG
                        ).show()
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
}

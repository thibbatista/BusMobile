package com.thiagosantos.gmaps

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.thiagosantos.gmaps.adapter.MarkerInfoAdapter
import com.thiagosantos.gmaps.helper.BitmapHelper
import com.thiagosantos.gmaps.model.L
import com.thiagosantos.gmaps.model.Parada
import com.thiagosantos.gmaps.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity() : AppCompatActivity() {


    private lateinit var mMap: GoogleMap

    private val places = MutableLiveData<List<L>>()

    private val paradas = MutableLiveData<List<Parada>>()

    private var localizacaoVeiculos: ((Posicao) -> Unit)? = null

    private val TAG = "DEBUG"

    override fun onStart() {
        super.onStart()


    }

    override fun onResume() {
        super.onResume()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ApiService.instancia(applicationContext)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            addMarkersParadas(googleMap)

            googleMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()
                bounds.include(LatLng(-23.516592, -46.698575))
                googleMap.moveCamera(
                       CameraUpdateFactory.newLatLngBounds(
                            bounds.build(),
                         100
                       )
                   )


                // chamadas call back de todas as paradas
//
//                paradas.observe(this) { parada ->
//                    parada.forEach { p ->
//                        bounds.include(LatLng(p.latitude, p.longitude))
//                    }
//                    googleMap.moveCamera(
//                        CameraUpdateFactory.newLatLngBounds(
//                            bounds.build(),
//                            100
//                        )
//                    )
//
//                }

//call back para camera maps , de todas as posiçoes dos veiculos
//                places.observe(this) { place ->
//                    place.forEach { l ->
//                        l.vs.forEach { vs ->
//                            bounds.include(LatLng(vs.py, vs.px))
//                        }
//
//                        googleMap.moveCamera(
//                            CameraUpdateFactory.newLatLngBounds(
//                                bounds.build(),
//                                100
//                            )
//                        )
//                    }
//                }

           }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            authKeyApi()

        }

        getParadas()

        //testar ver se pega o scopo dos marcadores
         mapFragment.getMapAsync{
            it.setOnInfoWindowClickListener {
                it.tag
                Log.d(TAG, "headers-debug: ${it.tag}")
            }
        }


    }



    private suspend fun authKeyApi() {
        try {
            val response = ApiService.autenticar()
            val cookie = response.headers().get("Set-Cookie")
            cookie?.let {
                ApiService.setCookie(cookie)
            } ?: Log.d(TAG, "Veio sem cookie")

            Log.d(TAG, "headers-debug: $cookie")
            Log.d(TAG, "API autenticada")
        } catch (e: Exception) {
            Log.d(TAG, "API fora do ar: " + e.message)
        }
    }

    //pega todas as posições dos onibus
    private fun getPosition() {
        lifecycleScope.launch {
            val response = ApiService.getPosicoes()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    places.postValue(body.l)

                    Log.d(ContentValues.TAG, "onCreate-> Teste saída: $body.l")

                }

            }
        }

    }

    //pega todas as paradas
    private fun getParadas() {

        lifecycleScope.launch {
            val response = ApiService.getParadas("0")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    paradas.postValue(body)

                    Log.d(ContentValues.TAG, "onCreate-> Teste saída: $body")

                }

            }
        }
    }

    private fun mapFragment(context: Context) {

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            addMarkers(googleMap)
            googleMap.setInfoWindowAdapter(MarkerInfoAdapter(context))

            googleMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()
                localizacaoVeiculos = { pos ->
                    pos.l.forEach { posL ->
                        val vs = posL.vs
                        vs.forEach { posLvs ->
                            bounds.include(LatLng(posLvs.py, posLvs.px))
                        }
                    }

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

    // adiciona marcadores para todas as posicoes
    private fun addMarkers(googleMap: GoogleMap) {

        places.observe(this) { place ->
            place.forEach { l ->
                l.vs.forEach { vs ->
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .title(vs.origem)
                            .snippet(vs.destino)
                            .position(LatLng(vs.py, vs.px))
                            .icon(
                                BitmapHelper.vectorToBitmap(
                                    this,
                                    R.drawable.ic_baseline_directions_bus_24,
                                    ContextCompat.getColor(this, R.color.teal_200)
                                )
                            )
                    )
                }
            }
        }

    }

    // adciona marcadores para todas as paradas
    private fun addMarkersParadas(googleMap: GoogleMap) {

        paradas.observe(this) { parada ->
            parada.forEach { p ->

                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(p.nome)
                        .snippet(p.enderecoParada)
                        .position(LatLng(p.latitude, p.longitude))
                        .icon(
                            BitmapHelper.vectorToBitmap(
                                this,
                                R.drawable.ic_baseline_directions_bus_24,
                                ContextCompat.getColor(this, R.color.teal_200)
                            )
                        )
                )
            }
        }
    }

}
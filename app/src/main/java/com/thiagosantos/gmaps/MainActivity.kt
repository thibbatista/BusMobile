package com.thiagosantos.gmaps

import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import com.thiagosantos.gmaps.model.LinhasParadas
import com.thiagosantos.gmaps.model.Parada
import com.thiagosantos.gmaps.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class MainActivity() : AppCompatActivity() {

    private lateinit var mMap: GoogleMap

    private val places = MutableLiveData<List<L>>()

    private val paradas = MutableLiveData<List<Parada>>()

    //private val linhas = MutableLiveData<List<LinhasParadas>>()

    private var localizacaoVeiculos: ((Posicao) -> Unit)? = null

    private val TAG = "DEBUG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ApiService.instancia(applicationContext)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->

            googleMap.setInfoWindowAdapter(MarkerInfoAdapter(this))
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
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            authKeyApi()
            getParadas()


        }

//        lifecycleScope.launch{
//            getParadas()
//
//        }

        //getParadas()



        //testar ver se pega o scopo dos marcadores
        mapFragment.getMapAsync {
            it.setOnInfoWindowClickListener { marker ->
                Log.d(TAG, "Marker id------------>>>>>>: ${marker.title}")
                marker.title?.let { title -> iniciaListLinhasActivity(title.toInt()) }


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
//    private fun getPosition() {
//        lifecycleScope.launch {
//            val response = ApiService.getPosicoes()
//
//            if (response.isSuccessful) {
//                val body = response.body()
//                if (body != null) {
//                    places.postValue(body.l)
//
//                    Log.d(ContentValues.TAG, "onCreate-> Teste saída: $body.l")
//
//                }
//
//            }
//        }
//
//    }

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


    //pega todas as linhas de uma parada, com previsao de chegada
    private fun getLinhas(id: String) {

        lifecycleScope.launch {
            val codigo = id.toInt()
            val response = ApiService.getLinhas(codigo)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    places.postValue(body.parada.relacaoLinhas)

                    Log.d(ContentValues.TAG, "Lista de Linhas por Parada-> Teste saída: ${body}")

                }
            }

        }
    }

//    private fun mapFragment(context: Context) {
//
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map_fragment) as SupportMapFragment
//        mapFragment.getMapAsync { googleMap ->
//            addMarkers(googleMap)
//            googleMap.setInfoWindowAdapter(MarkerInfoAdapter(context))
//
//            googleMap.setOnMapLoadedCallback {
//                val bounds = LatLngBounds.builder()
//                localizacaoVeiculos = { pos ->
//                    pos.l.forEach { posL ->
//                        val vs = posL.vs
//                        vs.forEach { posLvs ->
//                            bounds.include(LatLng(posLvs.py, posLvs.px))
//                        }
//                    }
//
//                    googleMap.moveCamera(
//                        CameraUpdateFactory.newLatLngBounds(
//                            bounds.build(),
//                            100
//                        )
//                    )
//                }
//            }
//        }
//    }

    // adiciona marcadores para todas as posicoes
//    private fun addMarkers(googleMap: GoogleMap) {
//
//        places.observe(this) { place ->
//            place.forEach { l ->
//                l.vs.forEach { vs ->
//                    val marker = googleMap.addMarker(
//                        MarkerOptions()
//                            .title(vs.origem)
//                            .snippet(vs.destino)
//                            .position(LatLng(vs.py, vs.px))
//                            .icon(
//                                BitmapHelper.vectorToBitmap(
//                                    this,
//                                    R.drawable.ic_baseline_directions_bus_24,
//                                    ContextCompat.getColor(this, R.color.teal_200)
//                                )
//                            )
//                    )
//                }
//            }
//        }
//
//    }


    // intent

    private fun iniciaListLinhasActivity(parada: Int) {
        val intent = Intent(this, ListLinhasAcitvity::class.java)
        intent.putExtra("parada", parada)
        startActivity(intent)
    }

    // adciona marcadores para todas as paradas
    private fun addMarkersParadas(googleMap: GoogleMap) {
        mMap = googleMap
        paradas.observe(this) { parada ->
            parada.forEach { p ->

                val marker = mMap.addMarker(
                    MarkerOptions()
                        .title(p.id.toString())
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
                if (marker != null) {
                    marker.tag = p
                }
            }
        }

    }
}
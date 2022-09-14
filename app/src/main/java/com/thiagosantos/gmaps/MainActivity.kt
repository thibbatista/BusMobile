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
import com.thiagosantos.gmaps.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity() : AppCompatActivity() {

    private val places = MutableLiveData<List<L>>()

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
            addMarkers(googleMap)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            autenticaKeyApi()

        }

        pegaRespostaApi()

    }


    private suspend fun autenticaKeyApi() {
        try {
            val resposta = ApiService.autenticar()
            val biscoitinho = resposta.headers().get("Set-Cookie")
            biscoitinho?.let {
                ApiService.setCookie(biscoitinho)
            } ?: Log.d(TAG, "Veio sem cookie")

            Log.d(TAG, "headers-debug: $biscoitinho")
            Log.d(TAG, "API autenticada")
        } catch (e: Exception) {
            Log.d(TAG, "API fora do ar: " + e.message)
        }
    }


    private fun pegaRespostaApi() {
        lifecycleScope.launch {
            val resposta = ApiService.getPosicoes()

            if (resposta.isSuccessful) {
                val response = resposta.body()
                if (response != null) {
                    localizacaoVeiculos?.invoke(response)

                    places.postValue(response.l)

                    Log.d(ContentValues.TAG, "onCreate-> Teste saída: $response.l")

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

    private fun addMarkers(googleMap: GoogleMap) {

        places.observe(this){
            it.forEach { l->
                l.vs.forEach{ vs->
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .title(vs.origem)
                            .snippet(vs.destino)
                            .position(LatLng(vs.py, vs.px))
                    )
                }
            }
        }

//        places.forEach { place ->
//
//            val latLng = place.vs
//            latLng.forEach { vs ->
//
////                val marker = googleMap.addMarker(
////                    MarkerOptions()
////                        .title(place.c)
////                        .snippet(place.lt0)
////                        .position(LatLng(vs.py, vs.px))
//
//                )
//
//            }
//        }


//
//        localizacaoVeiculos = { pos ->
//
//            pos.l.forEach { place ->
//
//                val latLng = place.vs
//                latLng.forEach { vs ->
//
//                    val marker = googleMap.addMarker(
//                        MarkerOptions()
//                            .title(place.c)
//                            .snippet(place.lt0)
//                            .position(LatLng(vs.py, vs.px))
//
//                            )
//
//                }
//            }
//
//        }
    }
}
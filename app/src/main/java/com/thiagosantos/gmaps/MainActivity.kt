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

            googleMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()

                places.observe(this){ place ->
                    place.forEach { l ->
                        l.vs.forEach{ vs->
                            bounds.include(LatLng(vs.py, vs.px))
                        }

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),100))
                    }
                }

            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            authKeyApi()

        }

        getPosition()

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
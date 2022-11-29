package com.thiagosantos.gmaps

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
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
import com.thiagosantos.gmaps.adapter.MarkerInfoAdapter
import com.thiagosantos.gmaps.helper.BitmapHelper
import com.thiagosantos.gmaps.model.L
import com.thiagosantos.gmaps.model.Parada
import com.thiagosantos.gmaps.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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


        loadApis()

        val textview = findViewById<TextView>(R.id.marqueeText)
        textview.text = getAddress(LatLng(-23.516592, -46.698575))
        textview.isSelected = true;

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            loadApis()
        }

    }

    private fun loadApis() {

        if (checkForInternet(this)) {
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()

            lifecycleScope.launch(Dispatchers.IO) {
                authKeyApi()
                getParadas()
            }

            loadMap()


        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show()
        }
    }
//
//    // overrride menu toolbar
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//
//        menuInflater.inflate(R.menu.main_menu, menu);
//
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        when (item.itemId) {
//            R.id.shareButton -> {
//
//                loadApis()
//
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

    private fun loadMap() {


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->

            googleMap.setInfoWindowAdapter(MarkerInfoAdapter(this))
            addMarkersParadas(googleMap)


            // getAddress(LatLng(-23.516592, -46.698575))


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

        mapFragment.getMapAsync {
            it.setOnInfoWindowClickListener { marker ->
                Log.d(TAG, "Marker id------------>>>>>>: ${marker.title}")
                Log.d(TAG, "Nome da Parada------------>>>>>>: ${marker.snippet}")
                Log.d(
                    TAG,
                    "endereço da localizacao=========>>>>>> " + getAddress(
                        LatLng(
                            -23.516592,
                            -46.698575
                        )
                    )
                )

                marker.title?.let { it1 ->
                    marker.snippet?.let { it2 ->
                        iniciaListLinhasActivity(
                            it1.toInt(),
                            it2
                        )
                    }
                }


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
            Toast.makeText(
                applicationContext,
                "Hummm!, você está sem sinal de Internet.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "API fora do ar: " + e.message)
        }
    }


    //pega todas as paradas
    private fun getParadas() {

        try {
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

        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Hummm!, você está sem sinal de Internet.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "API fora do ar: " + e.message)
        }
    }


    // pega endereço de uma localizacao
    private fun getAddress(latLng: LatLng): String {


        val geocoder = Geocoder(this, Locale.getDefault())
        val address: Address?
        var addressText = ""

        val addresses: List<Address>? =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                address = addresses[0]
                addressText = address.getAddressLine(0)
            } else {
                addressText = "its not appear"
            }
        }
        return addressText
    }




// intent

    private fun iniciaListLinhasActivity(parada: Int, endereco: String) {
        val intent = Intent(this, ListLinhasAcitvity::class.java)
        intent.putExtra("parada", parada)
        intent.putExtra("endereco", endereco)
        startActivity(intent)
    }

    private fun iniciaListLinhasActivity2(endereco: String) {
        val intent = Intent(this, ListLinhasAcitvity::class.java)
        intent.putExtra("endereco", endereco)
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




package com.thiagosantos.gmaps

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thiagosantos.gmaps.adapter.ListLinhasAdapter
import com.thiagosantos.gmaps.databinding.ActivityListLinhasBinding
import com.thiagosantos.gmaps.helper.LinhasHelper
import com.thiagosantos.gmaps.model.LinhasParadas
import com.thiagosantos.gmaps.services.ApiService
import kotlinx.coroutines.launch

class ListLinhasAcitvity() : AppCompatActivity() {

    private val listLinhas = MutableLiveData<LinhasParadas>()

    private lateinit var listLinhasAdapter: ListLinhasAdapter

    private lateinit var binding: ActivityListLinhasBinding

    private val TAG = "DEBUG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_linhas)
        binding = ActivityListLinhasBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (checkForInternet(this)) {
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()

            getLinhasIntent()



        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show()
        }






        listLinhas.observe(this) { linhasParadas ->

            listLinhasAdapter = ListLinhasAdapter(this, linhasParadas)
            binding.rvPrevisao.layoutManager = LinearLayoutManager(this)
            binding.rvPrevisao.adapter = listLinhasAdapter

            listLinhasAdapter.onItemClick = { linhaHelper ->

                iniciaListLinhasActivity(linhaHelper)
            }
        }
    }

    private fun getLinhasIntent(){
        val dados = intent.extras
        val codigoParada = dados?.getInt("parada")
        val enderecoParada = dados?.getString("endereco")

        Log.d("DEBUG", "Endereço da Parada obtido da MAIN------===============>>>>>>>>>>>>>>####*******#**#*#*#*.>>>>>>>: $enderecoParada")

        Log.d("DEBUG", "Codigo da parada captura da acitivity Main: $codigoParada")

        val letreiro = findViewById<TextView>(R.id.marqueeText)
        letreiro.text = enderecoParada
        letreiro.isSelected = true;


        if (codigoParada != null) {

            lifecycleScope.launch {
                getLinhas(codigoParada)

            }


            //refresh button
            val fab = findViewById<FloatingActionButton>(R.id.fab)
            fab.setOnClickListener{



                if (checkForInternet(this)) {
                    Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()

                    getLinhas(codigoParada)



                } else {
                    Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show()
                }



            }

        }
    }


    //pega todas as linhas de uma parada, com previsao de chegada
    private fun getLinhas(id: Int) {

        try {

            lifecycleScope.launch {

                val response = ApiService.getLinhas(id)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        listLinhas.postValue(body)


                        Log.d(ContentValues.TAG, "Lista de Linhas por Parada-> Teste saída: ${body}")
                    }
                }
            }
        }catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Hummm!, você está sem sinal de Internet.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "API fora do ar: " + e.message)
        }

    }


    //passa o objeto linhaHelper para activity GetLinhaActivity

    private fun iniciaListLinhasActivity(linhasHelper: LinhasHelper) {
        val intent = Intent(this, LinhaGps::class.java)
        intent.putExtra("linha", linhasHelper)
        startActivity(intent)
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
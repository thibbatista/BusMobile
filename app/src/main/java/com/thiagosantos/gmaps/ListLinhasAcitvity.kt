package com.thiagosantos.gmaps

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_linhas)
        binding = ActivityListLinhasBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getLinhasIntent()




//
//        val dados = intent.extras
//        val codigoParada = dados?.getInt("parada")
//
//        Log.d("DEBUG", "Codigo da parada captura da acitivity Main: $codigoParada")
//
//        if (codigoParada != null) {
//
//            lifecycleScope.launch {
//                getLinhas(codigoParada)
//            }
//        }

        listLinhas.observe(this) { linhasParadas ->

            listLinhasAdapter = ListLinhasAdapter(this, linhasParadas)
            binding.rvPrevisao.layoutManager = LinearLayoutManager(this)
            binding.rvPrevisao.adapter = listLinhasAdapter

            listLinhasAdapter.onItemClick = { linhaHelper ->

                iniciaListLinhasActivity(linhaHelper)
            }
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
//                getLinhasIntent()
//
//
//               // Log.d("DEBUG", "Botao rfresh pressionado PAGINA LISTA ACTIVITY")
//
//                // para abrir tela de compartilhamento
////                val sharingIntent = Intent(Intent.ACTION_SEND)
////
////                // type of the content to be shared
////                sharingIntent.type = "text/plain"
////
////                // Body of the content
////                val shareBody = "Your Body Here"
////
////                // subject of the content. you can share anything
////                val shareSubject = "Your Subject Here"
////
////                // passing body of the content
////                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
////
////                // passing subject of the content
////                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
////                startActivity(Intent.createChooser(sharingIntent, "Share using"))
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

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

                //refresh button
                val fab = findViewById<FloatingActionButton>(R.id.fab)
                fab.setOnClickListener{
                    getLinhas(codigoParada)
                }
            }
        }
    }

    private fun refreshButtonIsPressed(item: Boolean): Boolean{
        return item
    }


    //pega todas as linhas de uma parada, com previsao de chegada
    private fun getLinhas(id: Int) {

        lifecycleScope.launch {
            //val codigo = id.toInt()
            val response = ApiService.getLinhas(id)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    listLinhas.postValue(body)
                    //places.postValue(body.p.l)

                    Log.d(ContentValues.TAG, "Lista de Linhas por Parada-> Teste saída: ${body}")
                }
            }
        }
    }


    //passa o objeto linhaHelper para activity GetLinhaActivity

    private fun iniciaListLinhasActivity(linhasHelper: LinhasHelper) {
        val intent = Intent(this, LinhaGps::class.java)
        intent.putExtra("linha", linhasHelper)
        startActivity(intent)
    }
}
package com.thiagosantos.gmaps

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

    //private lateinit var linhasHelper: LinhasHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_linhas)
        binding = ActivityListLinhasBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dados = intent.extras
        val codigoParada = dados?.getInt("parada")

        Log.d("DEBUG", "Codigo da parada captura da acitivity Main: $codigoParada")

        if (codigoParada != null) {

            lifecycleScope.launch {
                getLinhas(codigoParada)
            }

        }

        listLinhas.observe(this) { linhasParadas ->


            listLinhasAdapter = ListLinhasAdapter(this, linhasParadas)
            binding.rvPrevisao.layoutManager = LinearLayoutManager(this)
            binding.rvPrevisao.adapter = listLinhasAdapter


            // click e pega position da view
//
//            var letreiro: String?,
//            var codigoLinha: Int,
//            var destino: String?,
//            var origem: String?,
//            var sentido: Int,

            listLinhasAdapter.onItemClick = { linhaHelper ->
//
//                linhasHelper = LinhasHelper(linhasParadas.parada.relacaoLinhas[position].letreiro,)
//                // preenche o objeto linhasHelper que irá ser passado para a activity GetLinhaActivity
//                linhasHelper.codigoLinha = linhasParadas.parada.relacaoLinhas[position].codigoLinha
//                linhasHelper.destino = linhasParadas.parada.relacaoLinhas[position].destino
//                linhasHelper.origem = linhasParadas.parada.relacaoLinhas[position].origem
//                linhasHelper.sentido = linhasParadas.parada.relacaoLinhas[position].sentido
//                linhasHelper.letreiro = linhasParadas.parada.relacaoLinhas[position].letreiro


                iniciaListLinhasActivity(linhaHelper)

            }
        }


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
        val intent = Intent(this, GetLinhaActivity::class.java)
        intent.putExtra("linha", linhasHelper)
        startActivity(intent)
    }

}
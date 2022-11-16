package com.thiagosantos.gmaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.thiagosantos.gmaps.helper.LinhasHelper

class GetLinhaActivity : AppCompatActivity() {

    var linha: LinhasHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_linha)

        linha = intent.getParcelableExtra("linha")

        Log.d("imprimi objeto","Imprimi objeto LinhasHelper preenchido ---------------->>>>>${linha}")
    }
    
}

// TODO:  passar parada e linha, usar  /Previsao?codigoParada={codigoParada}&codigoLinha={codigoLinha}
// TODO:  usar motion layout, usar  nesta activity usar dois xml, um da dascrições da linha e outro com o mapa mostrando as posicões
// TODO: quando o usuario clicar no marker, exibir o horario previsto para a parada selecionada 
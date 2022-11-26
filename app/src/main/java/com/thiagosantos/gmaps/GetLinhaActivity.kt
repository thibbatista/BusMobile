package com.thiagosantos.gmaps

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thiagosantos.gmaps.helper.LinhasHelper


class GetLinhaActivity : AppCompatActivity() {

    var linha: LinhasHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_linha)

        linha = intent.getParcelableExtra("linha")

        Log.d("imprimi objeto","Imprimi objeto LinhasHelper preenchido ---------------->>>>>${linha}")

//        val textview = findViewById<TextView>(R.id.marqueeText)
//        textview.isSelected = true;

//
//
//        // initializing the TextView
//       val textview = findViewById<TextView>(R.id.marqueeText)
//        val text = "ola eu sou um texto scrolling , isso é um text"
//
//        textview.text = text
//
//        textview.movementMethod = ScrollingMovementMethod.getInstance()
    }
    
}

// TODO:  passar parada e linha, usar  /Previsao?codigoParada={codigoParada}&codigoLinha={codigoLinha}
// TODO:  usar motion layout, usar  nesta activity usar dois xml, um da dascrições da linha e outro com o mapa mostrando as posicões
// TODO: quando o usuario clicar no marker, exibir o horario previsto para a parada selecionada 
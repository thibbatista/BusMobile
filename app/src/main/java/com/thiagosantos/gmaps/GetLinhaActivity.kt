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
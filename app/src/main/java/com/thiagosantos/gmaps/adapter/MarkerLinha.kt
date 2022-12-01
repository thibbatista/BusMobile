package com.thiagosantos.gmaps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.thiagosantos.gmaps.R
import com.thiagosantos.gmaps.model.LinhaRastreio
import com.thiagosantos.gmaps.model.Parada
import com.thiagosantos.gmaps.model.Vs

class MarkerLinha (private val context: Context) : GoogleMap.InfoWindowAdapter {


    override fun getInfoContents(marker: Marker): View? {

        val place = marker.tag as? Vs ?: return null
        val view = LayoutInflater.from(context).inflate(R.layout.marker_linha, null)

        view.findViewById<TextView>(R.id.tv_linha).text = place.prefixoVeiculo.toString()
        view.findViewById<TextView>(R.id.tv_time).text = place.horarioPrevisto



        return view
    }

    override fun getInfoWindow(marker: Marker): View? = null


}

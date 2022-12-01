package com.thiagosantos.gmaps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.thiagosantos.gmaps.R
import com.thiagosantos.gmaps.model.Parada

class MarkerInfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {


    override fun getInfoContents(marker: Marker): View? {

        val place = marker.tag as? Parada ?: return null
        val view = LayoutInflater.from(context).inflate(R.layout.custon_marker_info, null)

        view.findViewById<TextView>(R.id.nomeParada).text = place.nome
        view.findViewById<TextView>(R.id.enderecoParada).text = place.enderecoParada

        return view
    }

    override fun getInfoWindow(marker: Marker): View? = null

}



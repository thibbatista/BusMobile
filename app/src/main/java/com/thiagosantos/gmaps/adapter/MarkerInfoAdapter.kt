package com.thiagosantos.gmaps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.thiagosantos.gmaps.Posicao

import com.thiagosantos.gmaps.R
import com.thiagosantos.gmaps.model.L

class MarkerInfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View? {

        val place = marker.tag as? L ?: return null
        val view = LayoutInflater.from(context).inflate(R.layout.custon_marker_info, null)

        view.findViewById<TextView>(R.id.txt_title).text = place.c
        view.findViewById<TextView>(R.id.txt_address).text = place.lt0
        view.findViewById<TextView>(R.id.txt_rating).text = context.getString(R.string.rating, place.qv)

        return view
    }

}
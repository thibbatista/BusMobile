package com.thiagosantos.gmaps.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable


data class Vs(
    val p: Int,
    val a: Boolean,
    val ta: String,
    val py: Double,
    val px: Double,
    val latLng: LatLng = LatLng(py,px)
) : Serializable {
    var origem = ""
    var destino = ""
}

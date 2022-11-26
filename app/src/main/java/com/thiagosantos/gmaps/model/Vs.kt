package com.thiagosantos.gmaps.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Vs(
    @SerializedName("p")
    val prefixoVeiculo: Int,
    @SerializedName("t")
    val horarioPrevisto: String,
    @SerializedName("a")
    val deficiente: Boolean,
    @SerializedName("ta")
    val horarioCapturadoUTC: String,
    @SerializedName("py")
    val latitude: Double,
    @SerializedName("px")
    val longitude: Double,
    val latLng: LatLng = LatLng(longitude,latitude)
) : Serializable
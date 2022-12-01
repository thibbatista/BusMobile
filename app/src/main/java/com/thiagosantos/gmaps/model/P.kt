package com.thiagosantos.gmaps.model

import com.google.gson.annotations.SerializedName


data class P(
    @SerializedName("cp")
    val codigoParada: Int,
    @SerializedName("l")
    val relacaoLinhas: List<L>,
    @SerializedName("np")
    val nomeParada: String,
    @SerializedName("px")
    val longitudeParada: Double,
    @SerializedName("py")
    val latitudeParada: Double
)
package com.thiagosantos.gmaps.model

import com.google.gson.annotations.SerializedName


data class V(
    @SerializedName("a")
    val acessivelDeficiente: Boolean,
    @SerializedName("p")
    val prefixoVeiculo: String,
    @SerializedName("px")
    val longitudeVeiculo: Double,
    @SerializedName("py")
    val latitudeVeiculo: Double,
    @SerializedName("t")
    val horarioPrevisto: String,
    @SerializedName("ta")
    val horarioCaptura: String
)
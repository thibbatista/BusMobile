package com.thiagosantos.gmaps.model

import com.google.gson.annotations.SerializedName


data class L(
    @SerializedName("c")
    val letreiro: String,
    @SerializedName("cl")
    val codigoLinha: Int,
    @SerializedName("lt0")
    val destino: String,
    @SerializedName("lt1")
    val origem: String,
    @SerializedName("qv")
    val quantidadeVeiculos: Int,
    @SerializedName("sl")
    val sentido: Int,
    @SerializedName("vs")
    val relacaoVeiculos: List<Vs>
)
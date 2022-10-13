package com.thiagosantos.gmaps.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class LinhasParadas(
    @SerializedName("hr")
    val hora: String,
    @SerializedName("p")
    val parada: P
)

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
    val relacaoVeiculos: List<V>
)

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
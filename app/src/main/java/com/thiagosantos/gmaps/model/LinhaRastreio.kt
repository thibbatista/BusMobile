package com.thiagosantos.gmaps.model

import com.google.gson.annotations.SerializedName


data class LinhaRastreio(
    @SerializedName("hr")
    val hora: String,
    @SerializedName("p")
    val parada: P
)



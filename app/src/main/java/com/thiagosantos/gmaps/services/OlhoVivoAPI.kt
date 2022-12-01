package com.thiagosantos.gmaps.services

import com.thiagosantos.gmaps.model.LinhaRastreio
import com.thiagosantos.gmaps.model.LinhasParadas
import com.thiagosantos.gmaps.model.Parada
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface OlhoVivoAPI {

    @GET("Parada/Buscar")
    suspend fun getParada(
        @Header("Cookie") credencial: String,
        @Query("termosBusca") nomeRua: String
    ): Response<List<Parada>>

    @GET("Previsao/Parada")
    suspend fun getLinhas(
        @Header("Cookie") credencial: String,
        @Query("codigoParada") id: Int
    ): Response<LinhasParadas>

    @GET("Previsao")
    suspend fun getPrevisaoLinhaParada(
        @Header("Cookie") credencial: String,
        @Query("codigoParada") p1: Int, @Query("codigoLinha") p2: Int
    ): Response<LinhaRastreio>

}
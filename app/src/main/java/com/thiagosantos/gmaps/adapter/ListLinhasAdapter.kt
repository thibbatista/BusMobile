package com.thiagosantos.gmaps.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thiagosantos.gmaps.databinding.LinhasBinding
import com.thiagosantos.gmaps.model.LinhasParadas
import java.text.SimpleDateFormat
import java.util.*

class ListLinhasAdapter(
    private val context: Context,
    private val linhasParadas: LinhasParadas
) : RecyclerView.Adapter<MainViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LinhasBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        val veiculos =  linhasParadas.parada.relacaoLinhas[position].relacaoVeiculos

        holder.codigoLinha.text = linhasParadas.parada.relacaoLinhas[position].codigoLinha.toString()
        holder.letreiro.text =  linhasParadas.parada.relacaoLinhas[position].destino
        holder.previsao.text = linhasParadas.parada.relacaoLinhas[position].relacaoVeiculos[0].horarioPrevisto
        holder.tempo.text = holder.currentDateAndTime

        if (veiculos.isNotEmpty() && veiculos.size >= 2){
            holder.prox1.text = veiculos[1].horarioPrevisto

        }else {
            holder.prox1.text = null
        }

        if (veiculos.isNotEmpty() && veiculos.size >= 3){
            holder.prox2.text = veiculos[2].horarioPrevisto

        }else {
            holder.prox2.text = null
        }

    }

    override fun getItemCount(): Int {
        return linhasParadas.parada.relacaoLinhas.size
    }
}



class MainViewHolder(binding: LinhasBinding) : RecyclerView.ViewHolder(binding.root) {
//    @SerializedName("hr")
//    val hora: String,
//    @SerializedName("p")
//    val parada: P
//    )
//
//    data class P(
//        @SerializedName("cp")
//        val codigoParada: Int,
//        @SerializedName("l")
//        val relacaoLinhas: List<L>,
//        @SerializedName("np")
//        val nomeParada: String,
//        @SerializedName("px")
//        val longitudeParada: Double,
//        @SerializedName("py")
//        val latitudeParada: Double
//    )
//
//    data class L(
//        @SerializedName("c")
//        val letreiro: String,
//        @SerializedName("cl")
//        val codigoLinha: Int,
//        @SerializedName("lt0")
//        val destino: String,
//        @SerializedName("lt1")
//        val origem: String,
//        @SerializedName("qv")
//        val quantidadeVeiculos: Int,
//        @SerializedName("sl")
//        val sentido: Int,
//        @SerializedName("vs")
//        val relacaoVeiculos: List<com.thiagosantos.gmaps.model.V>
//    )
//
//    data class V(
//        @SerializedName("a")
//        val acessivelDeficiente: Boolean,
//        @SerializedName("p")
//        val prefixoVeiculo: String,
//        @SerializedName("px")
//        val longitudeVeiculo: Double,
//        @SerializedName("py")
//        val latitudeVeiculo: Double,
//        @SerializedName("t")
//        val horarioPrevisto: String,
//        @SerializedName("ta")
//        val horarioCaptura: String

    val codigoLinha = binding.tvNumberLine
    val letreiro = binding.destiny
    val prox1 = binding.tvHour1
    val prox2 = binding.tvHour2
    val previsao = binding.tvTimeHour
    val tempo = binding.tvTime
    val deficiente = false
    val time = SimpleDateFormat("HH:mm")
    val currentDateAndTime = time.format(Date())

}
package com.thiagosantos.gmaps.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thiagosantos.gmaps.databinding.LinhasBinding
import com.thiagosantos.gmaps.helper.LinhasHelper
import com.thiagosantos.gmaps.model.LinhasParadas
import java.text.SimpleDateFormat
import java.util.*

class ListLinhasAdapter(
    private val context: Context,
    private val linhasParadas: LinhasParadas,

    var onItemClick: ((LinhasHelper) -> Unit)? = null

) : RecyclerView.Adapter<MainViewHolder>() {

    private lateinit var linhasHelper: LinhasHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LinhasBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        holder.itemView.setOnClickListener {

            linhasHelper = LinhasHelper(
                linhasParadas.parada.relacaoLinhas[position].letreiro,
                linhasParadas.parada.relacaoLinhas[position].codigoLinha,
                linhasParadas.parada.codigoParada,
                linhasParadas.parada.relacaoLinhas[position].destino,
                linhasParadas.parada.relacaoLinhas[position].origem,
                linhasParadas.parada.relacaoLinhas[position].sentido

            )

            onItemClick?.invoke(linhasHelper)

        }

        val veiculos = linhasParadas.parada.relacaoLinhas[position].relacaoVeiculos
        val horarioPrevisto =
            linhasParadas.parada.relacaoLinhas[position].relacaoVeiculos[0].horarioPrevisto

        holder.codigoLinha.text =
            linhasParadas.parada.relacaoLinhas[position].codigoLinha.toString()
        holder.letreiro.text = linhasParadas.parada.relacaoLinhas[position].destino
        holder.previsao.text = horarioPrevisto
        holder.tempo.text =
            getDuracao(holder.currentDateAndTime, horarioPrevisto).toString() + " min"


        if (veiculos.isNotEmpty() && veiculos.size >= 2) {
            holder.prox1.text = veiculos[1].horarioPrevisto

        } else {
            holder.prox1.text = null
        }

        if (veiculos.isNotEmpty() && veiculos.size >= 3) {
            holder.prox2.text = veiculos[2].horarioPrevisto

        } else {
            holder.prox2.text = null
        }

    }

    override fun getItemCount(): Int {
        return linhasParadas.parada.relacaoLinhas.size
    }
}

// subtracao de horas

fun getDuracao(hour1: String, hour2: String): Long {

    val format = SimpleDateFormat("HH:mm")
    val time1 = format.parse(hour1).time
    val time2 = format.parse(hour2).time

    val duracao = time2 - time1

    return duracao / 60000

}

class MainViewHolder(binding: LinhasBinding) : RecyclerView.ViewHolder(binding.root) {

    val codigoLinha = binding.tvNumberLine
    val letreiro = binding.destiny
    val prox1 = binding.tvHour1
    val prox2 = binding.tvHour2
    val previsao = binding.tvTimeHour
    val tempo = binding.tvTime
    val time = SimpleDateFormat("HH:mm")
    val currentDateAndTime = time.format(Date())

}
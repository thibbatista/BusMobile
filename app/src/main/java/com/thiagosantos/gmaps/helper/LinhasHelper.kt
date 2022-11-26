package com.thiagosantos.gmaps.helper

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LinhasHelper(
    var letreiro: String?,
    var codigoLinha: Int,
    var codigoParada: Int,
    var destino: String?,
    var origem: String?,
    var sentido: Int
): Parcelable
package com.example.demosrobotsciente

import com.google.gson.annotations.SerializedName


data class FlaskResponse(
    @SerializedName("miParametroMiEventoNombreDelEvento") val nombreEvento: String,
    @SerializedName("miParametroApodoUsuario") val apodoUsuario: String,
    @SerializedName("miParametroEstado") val estado: String,
    @SerializedName("miParametroIdRobot") val idRobot: String,
    @SerializedName("miParametroMac") val mac: String,
    @SerializedName("miParametroCorreoElectronicoDelAdministrador") val correoAdministrador: String,
    @SerializedName("miParametroCodigoQR") val codigoQR: String,
    @SerializedName("miParametroFotoDelRobot") val fotoDelRobot: String,
    @SerializedName("miParametroCSRFtoken") val csrfToken: String
)

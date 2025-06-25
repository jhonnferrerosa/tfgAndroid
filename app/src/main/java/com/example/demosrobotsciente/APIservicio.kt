package com.example.demosrobotsciente

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Body

import retrofit2.http.Url

interface APIservicio {
    @GET
    suspend fun funcion_registrarse(@Url parametroAPIservicioUrl: String): Response<FlaskResponse>

    @POST
    suspend fun funcion_aceptarRobot(@Header("X-CSRFToken") parametroAPIservicioCsrfToken: String, @Url parametroAPIservicioUrl: String, @Body parametroAPIservicioPeticionBody: PeticionBody): Response<FlaskResponse>

    @POST
    suspend fun funcion_rechazarRobot(@Header("X-CSRFToken") parametroAPIservicioCsrfToken: String, @Url parametroAPIservicioUrl: String, @Body parametroAPIservicioPeticionBody: PeticionBody): Response<FlaskResponse>
}
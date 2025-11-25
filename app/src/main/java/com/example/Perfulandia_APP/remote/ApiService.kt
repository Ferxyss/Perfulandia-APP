package com.example.Perfulandia_APP.remote

import com.example.Perfulandia_APP.model.SolicitudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/solicitudes")
    suspend fun createSolicitud(@Body solicitud: SolicitudDto): Response<SolicitudDto>

    @GET("api/solicitudes")
    suspend fun getSolicitudes(@Query("email") email: String? = null): Response<List<SolicitudDto>>

    @DELETE("api/solicitudes/{id}")
    suspend fun deleteSolicitud(@Path("id") id: String): Response<Unit>

}

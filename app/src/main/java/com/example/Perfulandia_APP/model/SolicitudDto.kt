package com.example.Perfulandia_APP.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SolicitudDto(
    @Json(name = "_id") val id: String? = null,
    val emailUsuario: String,
    val asunto: String,
    val mensaje: String,
    val createdAt: String? = null
)

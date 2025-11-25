package com.example.Perfulandia_APP.ui.screen

import com.example.Perfulandia_APP.model.SolicitudDto
import com.example.Perfulandia_APP.model.Solicitud

data class SolicitudState(
    val form: SolicitudForm = SolicitudForm(),
    val errors: Map<String, String> = emptyMap(),
    val generalError: String? = null,
    val submitting: Boolean = false,
    val success: Boolean = false,
    val remoteItems: List<SolicitudDto> = emptyList(),
    val localItems: List<Solicitud> = emptyList()
)
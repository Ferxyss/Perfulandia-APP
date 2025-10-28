package com.example.Perfulandia_APP.repository

import com.example.Perfulandia_APP.model.AppDatabase
import com.example.Perfulandia_APP.model.Solicitud
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SolicitudRepositorio(private val db: AppDatabase) {

    suspend fun crearSolicitud(
        correoUsuario: String,
        asunto: String,
        mensaje: String
    ) {
        withContext(Dispatchers.IO) {
            db.solicitudDao().insertSolicitud(
                Solicitud(
                    correoUsuario = correoUsuario,
                    asunto = asunto,
                    mensaje = mensaje,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun obtenerSolicitudesDe(correoUsuario: String): List<Solicitud> {
        return withContext(Dispatchers.IO) {
            db.solicitudDao().getSolicitudesPorUsuario(correoUsuario)
        }
    }
}

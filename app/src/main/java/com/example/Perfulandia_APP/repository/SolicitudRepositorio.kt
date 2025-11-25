package com.example.Perfulandia_APP.repository

import com.example.Perfulandia_APP.model.AppDatabase
import com.example.Perfulandia_APP.model.Solicitud
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.Perfulandia_APP.model.SolicitudDto
import com.example.Perfulandia_APP.remote.ApiService
import android.util.Log
import kotlin.math.abs

class SolicitudRepositorio(
    private val db: AppDatabase,
    private val api: ApiService
) {

    private fun normalizeEmail(raw: String): String =
        raw.trim().lowercase()

    suspend fun crearSolicitud(
        correoUsuario: String,
        asunto: String,
        mensaje: String
    ): Result<SolicitudDto?> {
        val correoNorm = normalizeEmail(correoUsuario)
        val dto = SolicitudDto(
            id = null,
            emailUsuario = correoNorm,
            asunto = asunto,
            mensaje = mensaje
        )
        return withContext(Dispatchers.IO) {
            try {
                val response = api.createSolicitud(dto)
                val responseBody = response.body()
                val remoteId: String? = responseBody?.id
                db.solicitudDao().insertSolicitud(
                    Solicitud(
                        correoUsuario = correoNorm,
                        asunto = asunto,
                        mensaje = mensaje,
                        timestamp = System.currentTimeMillis(),
                        remoteId = remoteId
                    )
                )
                if (response.isSuccessful) {
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Error HTTP ${response.code()}"))
                }

            } catch (e: Exception) {
                db.solicitudDao().insertSolicitud(
                    Solicitud(
                        remoteId = null,
                        correoUsuario = correoNorm,
                        asunto = asunto,
                        mensaje = mensaje,
                        timestamp = System.currentTimeMillis()
                    )
                )
                Result.failure(e)
            }
        }
    }

    suspend fun obtenerSolicitudesRemotas(correoUsuario: String): Result<List<SolicitudDto>> {
        val correoNorm = normalizeEmail(correoUsuario)
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getSolicitudes(if (correoNorm.isBlank()) null else correoNorm)
                Log.d("SolicitudRepo", "GET /api/solicitudes?email=${if (correoNorm.isBlank()) "<null>" else correoNorm} -> code=${response.code()}")
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()

                    if (correoNorm.isBlank()) {
                        Log.d("SolicitudRepo", "Borrando todas las solicitudes locales (sin filtro)")
                        db.solicitudDao().deleteAllSolicitudes()
                    } else {
                        Log.d("SolicitudRepo", "Borrando solicitudes locales para usuario=$correoNorm")
                        db.solicitudDao().deleteSolicitudesPorUsuario(correoNorm)
                    }

                    val solicitudesLocal = lista.map { dto ->

                        val parsedMillis: Long = try {
                            dto.createdAt?.let { txt ->
                                val patterns = arrayOf(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                                    "yyyy-MM-dd'T'HH:mm:ssXXX",
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                    "yyyy-MM-dd'T'HH:mm:ss'Z'"
                                )
                                var parsed: java.util.Date? = null
                                for (p in patterns) {
                                    try {
                                        val sdf = java.text.SimpleDateFormat(p, java.util.Locale.US)
                                        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                        parsed = sdf.parse(txt)
                                        if (parsed != null) break
                                    } catch (_: Exception) {

                                    }
                                }
                                parsed?.time ?: System.currentTimeMillis()
                            } ?: System.currentTimeMillis()
                        } catch (e: Exception) {
                            Log.e("SolicitudRepo", "Exception parsing createdAt='${dto.createdAt}': ${e.message}")
                            System.currentTimeMillis()
                        }

                        Log.d("SolicitudRepo", "Mapped remote item: email=${dto.emailUsuario}, asunto=${dto.asunto}, createdAt='${dto.createdAt}', parsedMillis=$parsedMillis")
                        Solicitud(
                            correoUsuario = dto.emailUsuario,
                            asunto = dto.asunto,
                            mensaje = dto.mensaje,
                            timestamp = parsedMillis,
                            remoteId = dto.id
                        )
                    }

                    if (solicitudesLocal.isNotEmpty()) {
                        db.solicitudDao().insertSolicitudes(solicitudesLocal)
                        Log.d("SolicitudRepo", "Inserted ${solicitudesLocal.size} solicitudes into local DB for user=$correoNorm")
                    } else {
                        Log.d("SolicitudRepo", "No remote solicitudes to insert for user=$correoNorm")
                    }

                    Result.success(lista)
                } else {
                    Result.failure(Exception("Error HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun obtenerSolicitudesDe(correoUsuario: String): List<Solicitud> {
        val correoNorm = normalizeEmail(correoUsuario)
        return withContext(Dispatchers.IO) {
            if (correoNorm.isBlank()) {
                db.solicitudDao().getAllSolicitudes()
            } else {
                db.solicitudDao().getSolicitudesPorUsuario(correoNorm)
            }
        }
    }

    suspend fun borrarSolicitudesDe(correoUsuario: String) {
        val correoNorm = normalizeEmail(correoUsuario)
        withContext(Dispatchers.IO) {
            db.solicitudDao().deleteSolicitudesPorUsuario(correoNorm)
        }
    }

    suspend fun borrarSolicitud(timestamp: Long, correoUsuario: String = "") {
        val correoNorm = normalizeEmail(correoUsuario)
        withContext(Dispatchers.IO) {
            Log.d("SolicitudRepo", "borrarSolicitud START timestamp=$timestamp correo=$correoNorm")

            try {
                val local = db.solicitudDao().getSolicitudPorTimestamp(timestamp)
                Log.d(
                    "SolicitudRepo",
                    "Local row for timestamp=$timestamp -> ${local?.let {
                        "id=${it.id}, remoteId=${it.remoteId}, correo=${it.correoUsuario}, asunto=${it.asunto}, timestamp=${it.timestamp}"
                    } ?: "null"}"
                )

                val remoteIdFromLocal = local?.remoteId
                if (!remoteIdFromLocal.isNullOrBlank()) {
                    try {
                        val delResp = api.deleteSolicitud(remoteIdFromLocal)
                        Log.d("SolicitudRepo", "DELETE remoto id=$remoteIdFromLocal -> code=${delResp.code()}")
                        if (delResp.isSuccessful || delResp.code() == 204) {
                            val rows = db.solicitudDao().deleteSolicitud(timestamp)
                            Log.d("SolicitudRepo", "DELETE LOCAL tras remote success rowsAffected=$rows timestamp=$timestamp")
                            return@withContext
                        } else if (delResp.code() == 404) {
                            Log.w("SolicitudRepo", "DELETE remoto 404 -> eliminar local igualmente")
                            val rows = db.solicitudDao().deleteSolicitud(timestamp)
                            Log.d("SolicitudRepo", "DELETE LOCAL (404) rowsAffected=$rows timestamp=$timestamp")
                            return@withContext
                        } else {
                            Log.w("SolicitudRepo", "DELETE remoto no exitoso (code=${delResp.code()}). Seguimos con fallback.")
                        }
                    } catch (e: Exception) {
                        Log.w("SolicitudRepo", "Exception during remote DELETE for id=$remoteIdFromLocal: ${e.message}")
                    }
                } else {
                    Log.d("SolicitudRepo", "remoteId local es null -> intentar fallback")
                }

                var remoteToDelete: String? = null
                try {
                    val resp = api.getSolicitudes(if (correoNorm.isBlank()) null else correoNorm)
                    if (resp.isSuccessful) {
                        val listaRemota = resp.body() ?: emptyList()
                        val toleranceMs = 2000L
                        for (dto in listaRemota) {
                            val parsedMillis: Long? = try {
                                dto.createdAt?.let { txt ->
                                    val patterns = arrayOf(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                                        "yyyy-MM-dd'T'HH:mm:ssXXX",
                                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                        "yyyy-MM-dd'T'HH:mm:ss'Z'"
                                    )
                                    var parsed: java.util.Date? = null
                                    for (p in patterns) {
                                        try {
                                            val sdf = java.text.SimpleDateFormat(p, java.util.Locale.US)
                                            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                            parsed = sdf.parse(txt)
                                            if (parsed != null) break
                                        } catch (_: Exception) { }
                                    }
                                    parsed?.time
                                }
                            } catch (e: Exception) { null }

                            if (parsedMillis != null && local != null && kotlin.math.abs(parsedMillis - local.timestamp) <= toleranceMs) {
                                remoteToDelete = dto.id
                                Log.d("SolicitudRepo", "Fallback MATCH by timestamp -> remoteId=$remoteToDelete")
                                break
                            }
                        }

                        if (remoteToDelete.isNullOrBlank() && local != null) {
                            val localAsunto = local.asunto.trim().lowercase()
                            val localMensaje = local.mensaje.trim().lowercase()
                            val localEmail = local.correoUsuario.trim().lowercase()
                            for (dto in listaRemota) {
                                val dtoEmail = dto.emailUsuario.trim().lowercase()
                                val dtoAsunto = dto.asunto.trim().lowercase()
                                val dtoMensaje = dto.mensaje.trim().lowercase()
                                if (dtoEmail == localEmail && dtoAsunto == localAsunto && dtoMensaje == localMensaje) {
                                    remoteToDelete = dto.id
                                    Log.d("SolicitudRepo", "Fallback MATCH by content -> remoteId=$remoteToDelete")
                                    break
                                }
                            }
                        }
                    } else {
                        Log.w("SolicitudRepo", "GET remoto para fallback NO exitoso code=${resp.code()}")
                    }
                } catch (e: Exception) {
                    Log.w("SolicitudRepo", "Error getting remote list during fallback: ${e.message}")
                }

                if (!remoteToDelete.isNullOrBlank()) {
                    try {
                        val delResp2 = api.deleteSolicitud(remoteToDelete)
                        Log.d("SolicitudRepo", "DELETE remoto (fallback) id=$remoteToDelete -> code=${delResp2.code()}")
                        if (delResp2.isSuccessful || delResp2.code() == 204 || delResp2.code() == 200 || delResp2.code() == 404) {
                            val rows = db.solicitudDao().deleteSolicitud(timestamp)
                            Log.d("SolicitudRepo", "DELETE LOCAL (fallback success) rowsAffected=$rows timestamp=$timestamp")
                            return@withContext
                        } else {
                            Log.w("SolicitudRepo", "DELETE remoto (fallback) no exitoso code=${delResp2.code()}")
                        }
                    } catch (e: Exception) {
                        Log.w("SolicitudRepo", "Exception during fallback remote DELETE id=$remoteToDelete: ${e.message}")
                    }
                } else {
                    Log.d("SolicitudRepo", "Fallback remoteToDelete=null for timestamp=$timestamp")
                }
                try {
                    val rows = db.solicitudDao().deleteSolicitud(timestamp)
                    Log.d("SolicitudRepo", "DELETE LOCAL (final fallback) rowsAffected=$rows timestamp=$timestamp")
                } catch (e: Exception) {
                    Log.e("SolicitudRepo", "Failed to delete local solicitud timestamp=$timestamp: ${e.message}")
                    throw e
                }
            } finally {
                Log.d("SolicitudRepo", "borrarSolicitud END timestamp=$timestamp")
            }
        }
    }
}

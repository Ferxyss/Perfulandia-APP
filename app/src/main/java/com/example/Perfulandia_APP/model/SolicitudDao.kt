package com.example.Perfulandia_APP.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SolicitudDao {

    @Insert
    suspend fun insertSolicitud(solicitud: Solicitud)

    @Query("SELECT * FROM solicitudes WHERE correoUsuario = :correo ORDER BY timestamp DESC")
    suspend fun getSolicitudesPorUsuario(correo: String): List<Solicitud>
}
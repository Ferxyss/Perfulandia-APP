package com.example.Perfulandia_APP.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SolicitudDao {

    @Insert
    suspend fun insertSolicitud(solicitud: Solicitud)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolicitudes(solicitudes: List<Solicitud>)

    @Query("SELECT * FROM solicitudes WHERE correoUsuario = :correo ORDER BY timestamp DESC")
    suspend fun getSolicitudesPorUsuario(correo: String): List<Solicitud>

    @Query("SELECT * FROM solicitudes ORDER BY timestamp DESC")
    suspend fun getAllSolicitudes(): List<Solicitud>

    @Query("DELETE FROM solicitudes WHERE correoUsuario = :correo")
    suspend fun deleteSolicitudesPorUsuario(correo: String)

    @Query("DELETE FROM solicitudes")
    suspend fun deleteAllSolicitudes()

    @Query("DELETE FROM solicitudes WHERE timestamp = :timestamp")
    suspend fun deleteSolicitud(timestamp: Long): Int

    @Query("SELECT * FROM solicitudes WHERE timestamp = :timestamp LIMIT 1")
    suspend fun getSolicitudPorTimestamp(timestamp: Long): Solicitud?

    @Query("SELECT * FROM solicitudes WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getSolicitudPorRemoteId(remoteId: String): Solicitud?
}

package com.example.Perfulandia_APP.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes")
data class Solicitud(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val correoUsuario: String,
    val asunto: String,
    val mensaje: String,
    val timestamp: Long
)
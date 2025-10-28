package com.example.Perfulandia_APP.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM users WHERE correo = :correo LIMIT 1")
    suspend fun buscarCorreo(correo: String): Usuario?
}

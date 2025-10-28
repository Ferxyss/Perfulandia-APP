package com.example.Perfulandia_APP.repository

import com.example.Perfulandia_APP.model.UserDao
import com.example.Perfulandia_APP.model.Usuario

class UsuarioRepositorio(private val dao: UserDao) {
    suspend fun register(user: Usuario) {
        if (dao.buscarCorreo(user.correo) != null) {
            throw IllegalArgumentException("El correo ya está registrado")
        }
        dao.insert(user)
    }

    suspend fun getUsuarioPorCorreo(correo: String): Usuario? {
        return dao.buscarCorreo(correo)
    }
}


package com.example.Perfulandia_APP

import android.app.Application
import com.example.Perfulandia_APP.model.AppDatabase
import com.example.Perfulandia_APP.repository.UsuarioRepositorio

class AppModule(app: Application) {
    private val db = AppDatabase.get(app)
    val usuarioRepositorio = UsuarioRepositorio(db.userDao())
}
package com.example.Perfulandia_APP

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.Perfulandia_APP.navegation.Navegacion
import com.example.Perfulandia_APP.viewmodel.RegisterViewModel
import com.example.Perfulandia_APP.viewmodel.SolicitudViewModel
import com.example.Perfulandia_APP.model.AppDatabase
import com.example.Perfulandia_APP.repository.UsuarioRepositorio
import com.example.Perfulandia_APP.repository.SolicitudRepositorio
import com.example.Perfulandia_APP.remote.RetrofitInstance

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.get(application)
        val usuarioRepo = UsuarioRepositorio(db.userDao())
        val registerVM = RegisterViewModel(usuarioRepo)

        val api = RetrofitInstance.create()
        val solicitudRepo = SolicitudRepositorio(db, api)
        val solicitudVM = SolicitudViewModel(solicitudRepo)

        setContent {
            val navController = rememberNavController()

            Navegacion(
                navController = navController,
                vm = registerVM,
                vm2 = solicitudVM
            )
        }
    }
}

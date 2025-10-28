package com.example.Perfulandia_APP


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.Perfulandia_APP.navegation.Navegacion
import com.example.Perfulandia_APP.viewmodel.RegisterViewModel


class MainActivity : ComponentActivity() {
    private lateinit var module: AppModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        module = AppModule(application)

        val vm = RegisterViewModel(module.usuarioRepositorio)

        setContent {
            val navController = rememberNavController()
            Navegacion(navController = navController, vm = vm)
        }
    }
}
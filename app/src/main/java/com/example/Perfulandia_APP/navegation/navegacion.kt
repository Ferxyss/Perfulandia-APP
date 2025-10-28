package com.example.Perfulandia_APP.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.Perfulandia_APP.ui.screen.LoginScreen
import com.example.Perfulandia_APP.ui.screen.RegisterScreen
import com.example.Perfulandia_APP.ui.screen.InicioScreen
import com.example.Perfulandia_APP.ui.screen.MenuScreen
import com.example.Perfulandia_APP.ui.screen.PerfilScreen
import com.example.Perfulandia_APP.ui.screen.SolicitudScreen
import com.example.Perfulandia_APP.ui.screen.rememberElegirFotoDeGaleriaLauncher
import com.example.Perfulandia_APP.viewmodel.RegisterViewModel
import com.example.Perfulandia_APP.ui.screen.rememberTomarFotoLauncher

@Composable
fun Navegacion(
    navController: NavHostController,
    vm: RegisterViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {


        composable("login") {
            LoginScreen(
                vm = vm,
                navController = navController
            )
        }


        composable("register") {
            RegisterScreen(
                vm = vm,
                navController = navController,
                onRegisterSuccess = { nombreUsuario ->
                    navController.navigate("welcome/$nombreUsuario") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "welcome/{nombre}",
            arguments = listOf(navArgument("nombre") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Usuario"

            InicioScreen(
                navController = navController,
                nombreUsuario = nombre
            )
        }

        composable("contacto") {
            MenuScreen(
                vm = vm,
                navController = navController
            )
        }
        composable("perfil") {
            val tomarFoto = rememberTomarFotoLauncher { uri ->
                vm.actualizarFotoPerfil(uri.toString())
            }
            val elegirFoto = rememberElegirFotoDeGaleriaLauncher { uri ->
                vm.actualizarFotoPerfil(uri.toString())
            }
            PerfilScreen(
                vm = vm,
                onTomarFoto = {
                    tomarFoto()
                },
                onElegirGaleria = {
                    elegirFoto()
                }
            )
        }
        composable("solicitudes") {
            SolicitudScreen(
                vm = vm,
                navController = navController
            )
        }
    }
}

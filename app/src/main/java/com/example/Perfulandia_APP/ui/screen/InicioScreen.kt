package com.example.Perfulandia_APP.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Perfulandia_APP.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


@Composable
fun InicioScreen(navController: NavController, nombreUsuario: String) {
    val cargando = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de Perfulandia",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Bienvenido a Perfulandia SPA, $nombreUsuario",
                style = MaterialTheme.typography.headlineSmall
            )
            if (!cargando.value) {
                Button(
                    onClick = {
                        cargando.value = true
                        scope.launch {
                            delay(2000)
                            navController.navigate("contacto")
                            cargando.value = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir a Formulario de Contacto")
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Preparando formulario de contacto...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}



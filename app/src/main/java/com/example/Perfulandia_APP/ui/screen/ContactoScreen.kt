package com.example.Perfulandia_APP.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.Perfulandia_APP.ui.theme.Pink80
import com.example.Perfulandia_APP.ui.theme.SoftWhite
import com.example.Perfulandia_APP.viewmodel.RegisterViewModel
import com.example.Perfulandia_APP.viewmodel.SolicitudViewModel
import kotlinx.coroutines.launch

@Composable
fun ContactoScreen(
    vm: RegisterViewModel,
    solicitudVm: SolicitudViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val correoSesionRaw = vm.state.value.form.correo
    val correoSesion = correoSesionRaw.trim().lowercase()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Pink80, SoftWhite)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Formulario de Contacto", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Tu Nombre") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Icono de persona"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Tu Correo Electrónico") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Icono de correo"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Mensaje") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (cargando) return@Button
                    cargando = true

                    scope.launch {
                        try {
                            val correoUsuarioRaw = if (correoSesion.isNotBlank()) correoSesion else correo
                            val correoParaGuardar = correoUsuarioRaw.trim().lowercase()

                            if (correoParaGuardar.isBlank()) {
                                Toast.makeText(context, "Ingresa un correo o inicia sesión", Toast.LENGTH_LONG).show()
                                cargando = false
                                return@launch
                            }
                            if (mensaje.trim().isBlank()) {
                                Toast.makeText(context, "Ingresa un mensaje", Toast.LENGTH_LONG).show()
                                cargando = false
                                return@launch
                            }

                            Log.d("CONTACTO", "Creando solicitud: $correoParaGuardar | ${nombre.ifBlank { "Contacto desde app" }} | $mensaje")

                            solicitudVm.crearYRecargar(
                                correoUsuario = correoParaGuardar,
                                asunto = nombre.ifBlank { "Contacto desde app" },
                                mensaje = mensaje
                            ) { success, err ->
                                if (success) {
                                    if (vm.state.value.form.correo.isBlank()) {
                                        vm.onEmailChange(correoParaGuardar)
                                    }

                                    Toast.makeText(context, "¡Mensaje enviado!", Toast.LENGTH_LONG).show()
                                    nombre = ""
                                    correo = ""
                                    mensaje = ""
                                } else {
                                    val errMsg = err ?: "Error al enviar"
                                    Log.e("CONTACTO", "Error creando solicitud: $errMsg")
                                    Toast.makeText(context, "Error: $errMsg", Toast.LENGTH_LONG).show()
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("CONTACTO", "Error creando solicitud (exception)", e)
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            cargando = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando…")
                } else {
                    Text("Enviar Mensaje")
                }
            }
        }
    }
}

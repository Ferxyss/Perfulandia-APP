package com.example.Perfulandia_APP.ui.screen

import BotonCargando
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Perfulandia_APP.ui.theme.Pink80
import com.example.Perfulandia_APP.ui.theme.SoftWhite
import com.example.Perfulandia_APP.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun LoginScreen(
    vm: RegisterViewModel,
    navController: NavController
) {
    val state by vm.state
    val ctx = LocalContext.current


    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    if (state.success) {
        val nombreParaWelcome = state.form.nombre.ifBlank { "Usuario" }
        LaunchedEffect(nombreParaWelcome) {
            Toast.makeText(ctx, "Sesión iniciada", Toast.LENGTH_SHORT).show()
            navController.navigate("welcome/$nombreParaWelcome") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Pink80, SoftWhite)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val esHorizontal = this.maxWidth > this.maxHeight

        if (esHorizontal) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Iniciar sesión",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Ingresa tu correo y contraseña para continuar.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(Modifier.width(32.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    FormularioDeLoginUI(
                        correo = correo,
                        onCorreoChange = { correo = it },
                        contrasena = contrasena,
                        onContrasenaChange = { contrasena = it },
                        submitting = state.submitting,
                        generalError = state.generalError,
                        onSubmit = { onResult ->
                            vm.login(correo, contrasena) { ok ->
                                onResult(ok)
                                if (!ok) {
                                    Toast.makeText(
                                        ctx,
                                        "Credenciales inválidas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        onIrARegistro = {
                            navController.navigate("register") {
                                popUpTo("login") { inclusive = false }
                            }
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Iniciar sesión",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(16.dp))

                FormularioDeLoginUI(
                    correo = correo,
                    onCorreoChange = { correo = it },
                    contrasena = contrasena,
                    onContrasenaChange = { contrasena = it },
                    submitting = state.submitting,
                    generalError = state.generalError,
                    onSubmit = { onResult ->
                        vm.login(correo, contrasena) { ok ->
                            onResult(ok)
                            if (!ok) {
                                Toast.makeText(
                                    ctx,
                                    "Credenciales inválidas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onIrARegistro = {
                        navController.navigate("register") {
                            popUpTo("login") { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FormularioDeLoginUI(
    correo: String,
    onCorreoChange: (String) -> Unit,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    submitting: Boolean,
    generalError: String?,
    onSubmit: (onResult: (Boolean) -> Unit) -> Unit,
    onIrARegistro: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val cargandoLogin = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = correo,
        onValueChange = onCorreoChange,
        label = { Text("Correo") },
        leadingIcon = { androidx.compose.material3.Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = contrasena,
        onValueChange = onContrasenaChange,
        label = { Text("Contraseña") },
        leadingIcon = { androidx.compose.material3.Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    AnimatedVisibility(visible = generalError != null) {
        Text(
            text = generalError ?: "",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    Spacer(Modifier.height(16.dp))

    BotonCargando(
        cargando = cargandoLogin.value || submitting,
        textoNormal = "Iniciar sesión",
        textoCargando = "Verificando tus credenciales...",
        onClick = {
            cargandoLogin.value = true
            scope.launch {
                onSubmit { ok ->
                    scope.launch {
                        cargandoLogin.value = false
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(24.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "¿No estás registrado?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onIrARegistro() }
        )
    }
}

package com.example.Perfulandia_APP.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Perfulandia_APP.ui.theme.Pink80
import com.example.Perfulandia_APP.ui.theme.SoftWhite
import com.example.Perfulandia_APP.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    vm: RegisterViewModel,
    navController: NavController,
    onRegisterSuccess: (String) -> Unit = {}
) {
    val state by vm.state
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current

    if (state.success) {
        Toast.makeText(ctx, "¡Registro exitoso!", Toast.LENGTH_LONG).show()
        onRegisterSuccess(state.form.nombre)
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
                    Text("Crear cuenta", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Ingresa tus datos para unirte a la comunidad de Perfulandia.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(Modifier.width(32.dp))

                Column(modifier = Modifier.weight(1f)) {
                    FormularioDeRegistroUI(
                        state = state,
                        vm = vm,
                        haptic = haptic,
                        onIrALogin = {
                            navController.navigate("login") {
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
                Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))

                FormularioDeRegistroUI(
                    state = state,
                    vm = vm,
                    haptic = haptic,
                    onIrALogin = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FormularioDeRegistroUI(
    state: RegisterState,
    vm: RegisterViewModel,
    haptic: HapticFeedback,
    onIrALogin: () -> Unit
) {
    OutlinedTextField(
        value = state.form.nombre,
        onValueChange = vm::onNameChange,
        label = { Text("Nombre") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        isError = state.errors["nombre"] != null,
        supportingText = {
            if (state.errors["nombre"] != null) {
                Text(state.errors["nombre"]!!.message)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = state.form.correo,
        onValueChange = vm::onEmailChange,
        label = { Text("Correo") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        isError = state.errors["correo"] != null,
        supportingText = {
            if (state.errors["correo"] != null) {
                Text(state.errors["correo"]!!.message)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = state.form.contrasena,
        onValueChange = vm::onPasswordChange,
        label = { Text("Contraseña") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = PasswordVisualTransformation(),
        isError = state.errors["contrasena"] != null,
        supportingText = {
            if (state.errors["contrasena"] != null) {
                Text(state.errors["contrasena"]!!.message)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    AnimatedVisibility(visible = state.generalError != null) {
        Text(
            text = state.generalError ?: "",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    Spacer(Modifier.height(16.dp))

    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            vm.submit()
        },
        enabled = !state.submitting,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Crear cuenta")
    }

    Spacer(Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "¿Ya tienes cuenta?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "Iniciar sesión",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onIrALogin() }
        )
    }
}

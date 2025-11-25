package com.example.Perfulandia_APP.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Perfulandia_APP.model.Solicitud
import com.example.Perfulandia_APP.ui.theme.Pink80
import com.example.Perfulandia_APP.ui.theme.SoftWhite
import com.example.Perfulandia_APP.viewmodel.SolicitudViewModel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    navController: NavController,
    vm: SolicitudViewModel
) {
    val s = vm.state.value
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        val correo = vm.state.value.form.correo.trim().lowercase()
        if (correo.isNotBlank()) {
            vm.loadSolicitudesRemotas(correo)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(Pink80, SoftWhite))
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Mis solicitudes") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            vm.loadSolicitudesRemotas(vm.state.value.form.correo)
                        }) {
                            Text("Actualizar")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text("Última solicitud enviada", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                when {
                    s.generalError != null -> {
                        Text(text = "Error al cargar: ${s.generalError}", color = MaterialTheme.colorScheme.error)
                    }

                    s.localItems.isEmpty() -> {
                        if (s.form.correo.isBlank()) {
                            Text("Envía una solicitud desde el formulario para verla aquí.")
                        } else {
                            Text("No hay solicitudes para este correo.")
                        }
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(s.localItems) { sol ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(text = sol.asunto, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                        Spacer(Modifier.height(4.dp))
                                        Text(text = sol.mensaje, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(Modifier.height(6.dp))
                                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).apply { timeZone = java.util.TimeZone.getDefault() }
                                        Text(text = sdf.format(java.util.Date(sol.timestamp)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = {
                                        android.util.Log.d("SolicitudUI", "Eliminar.clicked timestamp=${sol.timestamp}")
                                        vm.borrarSolicitud(sol.timestamp, vm.state.value.form.correo) { success: Boolean, err: String? ->
                                            if (success) {
                                                Toast.makeText(context, "Solicitud eliminada", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Error al eliminar: ${err ?: "unknown"}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar solicitud")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


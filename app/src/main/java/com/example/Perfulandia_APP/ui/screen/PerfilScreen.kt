package com.example.Perfulandia_APP.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.Perfulandia_APP.R
import com.example.Perfulandia_APP.ui.theme.Pink80
import com.example.Perfulandia_APP.ui.theme.SoftWhite
import com.example.Perfulandia_APP.viewmodel.RegisterViewModel

@Composable
fun PerfilScreen(
    vm: RegisterViewModel,
    onTomarFoto: () -> Unit = {},
    onElegirGaleria: () -> Unit = {}
) {
    val state by vm.state
    val nombreUsuario = state.form.nombre.ifBlank { "Usuario" }
    val correoUsuario = state.form.correo.ifBlank { "correo@perfulandia.cl" }


    var menuVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Pink80, SoftWhite)
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    color = Color.White.copy(alpha = 0.3f)
                ) {
                    val fotoActual = vm.fotoPerfilUri.value
                    if (fotoActual != null) {
                        Image(
                            painter = rememberAsyncImagePainter(fotoActual),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            menuVisible = true
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Editar foto",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Editar foto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = menuVisible,
                    onDismissRequest = { menuVisible = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tomar foto") },
                        onClick = {
                            menuVisible = false
                            onTomarFoto()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Elegir de galería") },
                        onClick = {
                            menuVisible = false
                            onElegirGaleria()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PerfilSectionHeader(titulo = "Mi perfil")

            PerfilRow(
                icon = Icons.Default.Person,
                etiqueta = "Nombre",
                valor = nombreUsuario
            )

            PerfilDivider()

            PerfilRow(
                icon = Icons.Default.Email,
                etiqueta = "E-mail",
                valor = correoUsuario,
                puedeContraer = true
            )

            PerfilDivider()

            PerfilRow(
                icon = Icons.Default.Lock,
                etiqueta = "Contraseña",
                valor = "••••••",
                puedeContraer = false
            )

            PerfilDivider()
            PerfilRowSimple(
                etiqueta = "Estado",
                valor = "Conectado",
                resaltado = true
            )

            PerfilDivider()
        }
    }
}

@Composable
private fun PerfilSectionHeader(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun PerfilDivider() {
    androidx.compose.material3.HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@Composable
private fun PerfilRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    etiqueta: String,
    valor: String,
    puedeContraer: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = etiqueta,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = if (puedeContraer) 1 else Int.MAX_VALUE,
                overflow = if (puedeContraer) TextOverflow.Ellipsis else TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun PerfilRowSimple(
    etiqueta: String,
    valor: String,
    resaltado: Boolean = false
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = if (resaltado) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onBackground
        )
    }
}

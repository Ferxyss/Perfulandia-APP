package com.example.Perfulandia_APP.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun rememberElegirFotoDeGaleriaLauncher(
    onFotoSeleccionada: (Uri) -> Unit
): () -> Unit {

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onFotoSeleccionada(uri)
        }
    }
    return remember {
        {
            pickImageLauncher.launch("image/*")
        }
    }
}
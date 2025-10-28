package com.example.Perfulandia_APP.ui.screen

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberTomarFotoLauncher(
    onFotoTomada: (Uri) -> Unit
): () -> Unit {
    val context = LocalContext.current

    var currentImageUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && currentImageUri != null) {
            onFotoTomada(currentImageUri!!)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            val file = File.createTempFile(
                "profile_photo",
                ".jpg",
                context.cacheDir
            )
            file.deleteOnExit()

            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )
            currentImageUri = uri
            takePictureLauncher.launch(uri)
        } else {
        }
    }
    return {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}
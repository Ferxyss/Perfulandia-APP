package com.example.Perfulandia_APP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Perfulandia_APP.model.Usuario
import com.example.Perfulandia_APP.repository.UsuarioRepositorio
import com.example.Perfulandia_APP.ui.screen.RegisterState
import com.example.Perfulandia_APP.ui.screen.validate
import kotlinx.coroutines.launch

class RegisterViewModel(private val repo: UsuarioRepositorio): ViewModel() {

    var state = androidx.compose.runtime.mutableStateOf(RegisterState())
        private set

    var fotoPerfilUri = androidx.compose.runtime.mutableStateOf<String?>(null)
        private set

    fun actualizarFotoPerfil(uri: String?) {
        fotoPerfilUri.value = uri
    }

    fun onNameChange(v: String) {
        state.value = state.value.copy(
            form = state.value.form.copy(nombre = v)
        )
    }

    fun onEmailChange(v: String) {
        state.value = state.value.copy(
            form = state.value.form.copy(correo = v)
        )
    }

    fun onPasswordChange(v: String) {
        state.value = state.value.copy(
            form = state.value.form.copy(contrasena = v)
        )
    }

    fun submit() {
        val errors = validate(state.value.form)
        if (errors.isNotEmpty()) {
            state.value = state.value.copy(
                errors = errors,
                generalError = null
            )
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(
                submitting = true,
                generalError = null
            )
            try {
                repo.register(
                    Usuario(
                        nombre = state.value.form.nombre.trim(),
                        correo = state.value.form.correo.trim(),
                        contrasena = state.value.form.contrasena
                    )
                )
                state.value = state.value.copy(
                    submitting = false,
                    success = true
                )
            } catch (e: Exception) {
                state.value = state.value.copy(
                    submitting = false,
                    generalError = e.message
                )
            }
        }
    }


    fun resetSession() {
        state.value = RegisterState()
        fotoPerfilUri.value = null
    }

    fun login(
        correo: String,
        clave: String,
        onResult: (Boolean) -> Unit
    ) {
        // Marcamos que estamos procesando y limpiamos error previo
        state.value = state.value.copy(
            submitting = true,
            generalError = null
        )

        viewModelScope.launch {

            val usuarioBD = repo.getUsuarioPorCorreo(correo.trim())

            if (usuarioBD != null && usuarioBD.contrasena == clave) {
                state.value = state.value.copy(
                    form = state.value.form.copy(
                        nombre = usuarioBD.nombre,
                        correo = usuarioBD.correo,
                        contrasena = "" // nunca dejamos la clave en memoria visible
                    ),
                    submitting = false,
                    success = true,
                    generalError = null,
                    errors = emptyMap()
                )

                onResult(true)

            } else {
                state.value = state.value.copy(
                    submitting = false,
                    success = false,
                    generalError = "Correo o contraseña incorrectos"
                )

                onResult(false)
            }
        }
    }
}

package com.example.Perfulandia_APP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import com.example.Perfulandia_APP.repository.SolicitudRepositorio
import kotlinx.coroutines.launch
import com.example.Perfulandia_APP.ui.screen.SolicitudForm
import com.example.Perfulandia_APP.ui.screen.SolicitudState
import com.example.Perfulandia_APP.model.Solicitud
import android.util.Log

class SolicitudViewModel(
    private val repo: SolicitudRepositorio
) : ViewModel() {

    var state = mutableStateOf(SolicitudState())
        private set

    fun onCorreoChange(v: String) {
        state.value = state.value.copy(
            form = state.value.form.copy(correo = v)
        )
    }

    fun onAsuntoChange(v: String) {
        state.value = state.value.copy(
            form = state.value.form.copy(asunto = v)
        )
    }

    fun onMensajeChange(v: String) {
        state.value = state.value.copy(
            form = state.value.form.copy(mensaje = v)
        )
    }

    private fun validateForm(form: SolicitudForm): Map<String, String> {
        val e = mutableMapOf<String, String>()

        if (form.correo.isBlank()) e["correo"] = "Correo requerido"
        if (form.asunto.isBlank()) e["asunto"] = "Asunto requerido"
        if (form.mensaje.isBlank()) e["mensaje"] = "Mensaje requerido"

        return e
    }

    fun crearYRecargar(
        correoUsuario: String,
        asunto: String,
        mensaje: String,
        onResult: ((Boolean, String?) -> Unit)? = null
    ) {
        val correoNorm = correoUsuario.trim().lowercase()
        viewModelScope.launch {
            state.value = state.value.copy(submitting = true, generalError = null)
            try {
                val res = repo.crearSolicitud(
                    correoUsuario = correoNorm,
                    asunto = asunto,
                    mensaje = mensaje
                )

                if (res.isSuccess) {
                    loadSolicitudesLocales(correoNorm)
                    state.value = state.value.copy(
                        submitting = false,
                        success = true
                    )
                    onResult?.invoke(true, null)
                } else {
                    val msg = res.exceptionOrNull()?.message
                    state.value = state.value.copy(
                        submitting = false,
                        generalError = msg
                    )
                    onResult?.invoke(false, msg)
                }
            } catch (e: Exception) {
                state.value = state.value.copy(
                    submitting = false,
                    generalError = e.message
                )
                onResult?.invoke(false, e.message)
            }
        }
    }


    fun loadSolicitudesLocales(correo: String) {
        val correoNorm = correo.trim().lowercase()
        viewModelScope.launch {
            state.value = state.value.copy(submitting = true)
            try {
                val lista: List<Solicitud> = repo.obtenerSolicitudesDe(correoNorm)
                Log.d("SolicitudVM", "loadSolicitudesLocales size=${lista.size} for=$correoNorm")
                state.value = state.value.copy(
                    submitting = false,
                    localItems = lista
                )
            } catch (e: Exception) {
                Log.e("SolicitudVM", "error cargando locales", e)
                state.value = state.value.copy(
                    submitting = false,
                    generalError = e.message
                )
            }
        }
    }

    fun loadSolicitudesRemotas(correo: String) {
        viewModelScope.launch {
            state.value = state.value.copy(submitting = true)

            try {
                val res = repo.obtenerSolicitudesRemotas(correo.trim())

                if (res.isSuccess) {
                    state.value = state.value.copy(
                        submitting = false,
                        remoteItems = res.getOrNull().orEmpty()
                    )

                    val correoNorm = correo.trim().lowercase()
                    loadSolicitudesLocales(correoNorm)
                } else {
                    state.value = state.value.copy(
                        submitting = false,
                        generalError = res.exceptionOrNull()?.message
                    )
                }

            } catch (e: Exception) {
                state.value = state.value.copy(
                    submitting = false,
                    generalError = e.message
                )
            }
        }
    }

    fun borrarSolicitud(
        timestamp: Long,
        correo: String,
        onDone: ((Boolean, String?) -> Unit)? = null
    ) {
        val correoNorm = correo.trim().lowercase()
        viewModelScope.launch {
            try {
                repo.borrarSolicitud(timestamp, correoNorm)
                loadSolicitudesLocales(correoNorm)
                onDone?.invoke(true, null)
            } catch (e: Exception) {
                Log.e("SolicitudVM", "Error al borrar solicitud", e)
                onDone?.invoke(false, e.message)
            }
        }
    }

    fun reset() {
        state.value = SolicitudState()
    }
}

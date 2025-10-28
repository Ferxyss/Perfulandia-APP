package com.example.Perfulandia_APP.ui.screen

data class FieldError(val message: String)

data class RegisterForm(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = ""
)

data class RegisterState(
    val form: RegisterForm = RegisterForm(),
    val errors: Map<String, FieldError> = emptyMap(),
    val submitting: Boolean = false,
    val success: Boolean = false,
    val generalError: String? = null
)

fun validate(form: RegisterForm): Map<String, FieldError> {
    val errs = mutableMapOf<String, FieldError>()
    if (form.nombre.isBlank()) errs["nombre"] = FieldError("Ingresa tu nombre")
    if (!form.correo.matches(Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")))
        errs["correo"] = FieldError("Correo no válido")
    if (form.contrasena.length < 6)
        errs["contrasena"] = FieldError("Mínimo 6 caracteres")
    return errs
}

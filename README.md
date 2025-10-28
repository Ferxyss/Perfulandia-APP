# Perfulandia App

Perfulandia App es una aplicación móvil desarrollada en **Kotlin con Jetpack Compose**.  

Este proyecto forma parte del trabajo académico.

---

## Características principales

- **Registro de usuario**
  - Nombre, correo y contraseña.
  - Validación de campos.
  - Datos almacenados localmente.

- **Inicio de sesión**
  - Autenticación local usando correo + contraseña.
  - Mensajes de error cuando las credenciales no son válidas.

- **Gestión de sesión**
  - Al iniciar sesión se muestra un mensaje de bienvenida con el nombre del usuario.
  - El usuario puede cerrar sesión desde el menú lateral (Cerrar sesión).

- **Perfil del usuario**
  - Muestra nombre, correo y estado.
  - Permite “Editar foto”.
  - Integra el uso de la **cámara nativa** para tomar una foto y asignarla como foto de perfil.

- **Menú lateral**
  - Acceso a opciones como Perfil, Configuración, Solicitudes y Cerrar sesión.
  - Encabezado del menú con nombre + correo del usuario.

- **Formulario de contacto**
  - Pantalla con formulario para enviar un mensaje a Perfulandia SPA (nombre, correo, mensaje).

- **Indicadores visuales**
  - Loader tipo barra de progreso lineal en distintas acciones:
    - Al navegar hacia Formulario de Contacto.
    - Al registrarse (Creando tu cuenta).
    - Al cerrar sesión (Cerrando sesión).

---

## Tecnologías utilizadas

- **Kotlin**
- **Jetpack Compose (Material 3)**
- **Room Database**
- **ViewModel (MVVM)**
- **ActivityResultContracts**
- **FileProvider**
- **Navigation Compose**

---

## Arquitectura

Estructura tipo **MVVM**:

- `model/` → Entidades y base de datos.
- `repository/` → Lógica de acceso a datos.
- `viewmodel/` → Control de estado y funciones de negocio.
- `ui/` → Pantallas y temas.
- `navigation/` → Rutas y navegación de pantallas.

---

## Estructura del proyecto

```
app/
├── model/
│   ├── AppDatabase.kt
│   ├── Usuario.kt
│   └── UserDao.kt
│
├── repository/
│   └── UsuarioRepositorio.kt
│
├── ui/
│   ├── screen/
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── InicioScreen.kt
│   │   ├── MenuScreen.kt
│   │   ├── PerfilScreen.kt
│   │   └── ContactoScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── navigation/
│   └── navegacion.kt
│
├── viewmodel/
│   └── RegisterViewModel.kt
│
└── res/
    ├── xml/
    │   └── provider_paths.xml
    ├── values/
    ├── drawable/
    └── mipmap/
```

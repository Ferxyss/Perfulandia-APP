# Perfulandia App

Perfulandia App es una aplicación móvil desarrollada en Kotlin + Jetpack Compose, con persistencia local mediante Room y consumo de un microservicio Node.js para gestionar solicitudes de contacto.
Proyecto académico.

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
  - Permite enviar solicitudes con correo, asunto y mensaje.
  - Envío al microservicio mediante Retrofit.
  - Almacenamiento local automático (Room).
  - Botón Actualizar sincroniza con el backend.
  - Listado de solicitudes locales.
  - Eliminación de solicitudes (DELETE remoto + local).

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
- **Retrofit + Moshi (Consumo del microservicio)**
- **MockK + JUnit5 + Kotest**
- **Compose UI Test (Instrumented Test)**

---

## Arquitectura

Estructura tipo **MVVM**:

- `model/` → Entidades y base de datos.
- `remote/` → Retrofit (ApiService + RetrofitInstance).
- `repository/` → Lógica de acceso a datos.
- `viewmodel/` → Control de estado y funciones de negocio.
- `ui/` → Pantallas y temas.
- `navigation/` → Rutas y navegación de pantallas.

---

## Funcionalidad de Solicitudes
Se agregó un flujo completo para gestionar solicitudes:

- **SolicitudForm:** Datos que el usuario escribe.
- **SolicitudState**  Estado de pantalla (lista local, errores, cargando).
- **SolicitudViewModel:** Maneja creación, carga remota/local y borrado.
- **SolicitudRepositorio:** Conecta Retrofit + Room.

## Crud soportado:

- **Create:** Enviar solicitud (POST + insert local).
- **Read:** Cargar solicitudes remotas (GET) + obtener locales.
- **Delete:** Eliminar solicitud (DELETE remoto + delete local).

---

## Comunicación con el Microservicio

**ApiService**

Interfaz donde se definen los endpoints:

```
@POST("/solicitudes")
suspend fun crearSolicitud(@Body dto: SolicitudDto): Response<Unit>

@GET("/solicitudes/{correo}")
suspend fun obtenerSolicitudes(@Path("correo") correo: String): Response<List<SolicitudDto>>
```
**RetrofitInstance**

Crea y configura Retrofit para conectarse al backend:

```
Retrofit.Builder()
    .baseUrl("http://10.0.2.2:4001")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```
--- 

## Estructura del proyecto

```
app/
├── keystore/
│   └── mi_app_key.jks
│
├── src/
│   ├── androidTest/
│   │   └── com/example/Perfulandia_APP/
│   │       └── SolicitudDaoTest.kt
│   │
│   ├── main/
│   │   └── java/com/example/Perfulandia_APP/
│   │       ├── model/
│   │       │   ├── AppDatabase.kt
│   │       │   ├── Usuario.kt
│   │       │   ├── UserDao.kt
│   │       │   ├── Solicitud.kt
│   │       │   ├── SolicitudDao.kt
│   │       │   └── SolicitudDto.kt
│   │       │
│   │       ├── remote/
│   │       │   ├── ApiService.kt
│   │       │   └── RetrofitInstance.kt
│   │       │
│   │       ├── repository/
│   │       │   ├── UsuarioRepositorio.kt
│   │       │   └── SolicitudRepositorio.kt
│   │       │
│   │       ├── viewmodel/
│   │       │   ├── RegisterViewModel.kt
│   │       │   ├── SolicitudViewModel.kt
│   │       │   ├── AppModule.kt
│   │       │   └── MainActivity.kt
│   │       │
│   │       ├── ui/
│   │       │   ├── screen/
│   │       │   │   ├── Animacion.kt
│   │       │   │   ├── Camera.kt
│   │       │   │   ├── galeria.kt
│   │       │   │   ├── LoginScreen.kt
│   │       │   │   ├── RegisterScreen.kt
│   │       │   │   ├── InicioScreen.kt
│   │       │   │   ├── MenuScreen.kt
│   │       │   │   ├── PerfilScreen.kt
│   │       │   │   ├── ContactoScreen.kt
│   │       │   │   ├── SolicitudScreen.kt
│   │       │   │   ├── SolicitudForm.kt
│   │       │   │   ├── SolicitudState.kt
│   │       │   │   └── Validation.kt
│   │       │   │
│   │       │   └── theme/
│   │       │       ├── Color.kt
│   │       │       ├── Theme.kt
│   │       │       └── Type.kt
│   │       │
│   │       └── navigation/
│   │           └── navegacion.kt
│   │
│   ├── test/
│   │   └── com/example/Perfulandia_APP/
│   │       ├── SolicitudRepositorioTest.kt
│   │       └── SolicitudViewModelTest.kt      

```
---

## Microservicio implementado
El backend gestiona solicitudes enviadas desde la app móvil.

### Endpoints Implementados

**Método** **Ruta**	  **Función**
POST	`Solicitudes/`	Crea una solicitud
GET	`Solicitudes/:email`	Lista solicitudes por correo
DELETE	`Solicitudes/:id`	Elimina una solicitud

---

## Cómo ejecutar el backend
### 1. Instalar dependencias
```bash
npm install
```
### 2. Ejecutar el servidor
```bash
node server.js
```
## Para Android
```Emulador
http://10.0.2.2:4001
```
--- 

## Prueba realizadas
### Pruebas unitarias
- SolicitudRepositorioTest.kt
- SolicitudViewModelTest.kt
- MockK para Retrofit
- Mocks del DAO
- Validación de flujos y estados
### Prueba instrumentada
- SolicitudDaoTest.kt
- Room en memoria (androidTest)

## Firma y APK
- Keystore generada: mi_app_key.jks
- APK firmado: `app/release/app-release.apk`
- Instalación exitosa en dispositivo físico

--- 

## Cómo ejecutar el proyecto (paso a paso)
### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/tu-repo.git
```
(También puedes descargar el `.zip` del repositorio y descomprimirlo).

---

### 2. Abrir el proyecto en Android Studio
- Abre **Android Studio**.  
- Selecciona **Open...** o **Open an Existing Project**.  
- Elige la carpeta raíz del proyecto (donde está `settings.gradle` o `build.gradle`).  
- Android Studio indexará el proyecto automáticamente.

---

### 3. Sincronizar Gradle
- Si aparece una barra amarilla arriba diciendo **Sync Now**, haz clic.  
- Espera a que se descarguen las dependencias y no queden errores en la consola de *Build*.

---

### 4. Configurar el dispositivo de prueba
**Opción A:** Crear un emulador  
`Device Manager → Create Virtual Device` → selecciona un teléfono y una imagen de sistema Android.  

**Opción B:** Conectar un teléfono físico  
Asegúrate de tener la **Depuración USB** activada.  

---

### 5. Ejecutar la app
- Asegúrate de que la configuración de ejecución sea el módulo `app`.  
- Selecciona el dispositivo /emulador en la barra superior.  
- Presiona el botón (*Run*).

---

## Creditos

**Fernanda Paredes**  
Proyecto académico - *Perfulandia SPA*

---


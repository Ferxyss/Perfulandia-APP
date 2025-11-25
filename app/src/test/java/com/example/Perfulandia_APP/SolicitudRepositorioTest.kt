package com.example.Perfulandia_APP

import com.example.Perfulandia_APP.model.AppDatabase
import com.example.Perfulandia_APP.model.Solicitud
import com.example.Perfulandia_APP.model.SolicitudDto
import com.example.Perfulandia_APP.model.SolicitudDao
import com.example.Perfulandia_APP.remote.ApiService
import com.example.Perfulandia_APP.repository.SolicitudRepositorio
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import okhttp3.ResponseBody
import android.util.Log
import io.mockk.mockkStatic

@OptIn(ExperimentalCoroutinesApi::class)
class SolicitudRepositorioTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var scope: TestScope

    @MockK
    lateinit var api: ApiService

    @MockK
    lateinit var db: AppDatabase

    @MockK
    lateinit var dao: SolicitudDao

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)

        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0

        MockKAnnotations.init(this, relaxUnitFun = true)
        every { db.solicitudDao() } returns dao

        scope = TestScope(dispatcher)
    }

    @Test
    fun `crearSolicitud - éxito remoto inserta local y retorna DTO`() = runTest(dispatcher) {
        val dto = SolicitudDto(id = "abc123", emailUsuario = "Test@Example.COM", asunto = "Hola", mensaje = "Mensaje", createdAt = null)
        val response = Response.success(dto)

        coEvery { api.createSolicitud(any()) } returns response
        coEvery { dao.insertSolicitud(any()) } returns Unit

        val repo = SolicitudRepositorio(db, api)

        val res = repo.crearSolicitud(" TEST@Example.COM ", "Asunto", "Msg")
        res.isSuccess shouldBe true
        val body = res.getOrNull()
        body?.id shouldBe "abc123"


        coVerify { dao.insertSolicitud(match { it.correoUsuario == "test@example.com" && it.asunto == "Asunto" }) }
    }

    @Test
    fun `crearSolicitud - fallo remoto inserta local y retorna failure`() = runTest(dispatcher) {
        val errorBody = ResponseBody.create(null, "server error")
        val response = Response.error<SolicitudDto>(500, errorBody)

        coEvery { api.createSolicitud(any()) } returns response
        coEvery { dao.insertSolicitud(any()) } returns Unit

        val repo = SolicitudRepositorio(db, api)

        val res = repo.crearSolicitud("a@b.com", "x", "y")
        res.isFailure shouldBe true
        coVerify { dao.insertSolicitud(any()) }
    }

    @Test
    fun `obtenerSolicitudesRemotas - mapea createdAt y guarda en BD`() = runTest(dispatcher) {
        val createdAt = "2023-01-01T12:00:00.000Z"
        val dto = SolicitudDto(id = "r1", emailUsuario = "Usr@Dom.com", asunto = "A", mensaje = "M", createdAt = createdAt)
        val response = Response.success(listOf(dto))

        coEvery { api.getSolicitudes(any()) } returns response
        coEvery { dao.deleteSolicitudesPorUsuario(any()) } returns Unit
        coEvery { dao.insertSolicitudes(any()) } returns Unit

        val repo = SolicitudRepositorio(db, api)

        val res = repo.obtenerSolicitudesRemotas("USR@dom.com")
        res.isSuccess shouldBe true
        val list = res.getOrNull()
        list?.size shouldBe 1

        coVerify { dao.deleteSolicitudesPorUsuario("usr@dom.com") }
        coVerify { dao.insertSolicitudes(match { it.isNotEmpty() && it[0].remoteId == "r1" }) }
    }

    @Test
    fun `borrarSolicitud - si remoteId existe intenta DELETE remoto y borra local`() = runTest(dispatcher) {
        val timestamp = System.currentTimeMillis()
        val local = Solicitud(id = 1L, remoteId = "RID", correoUsuario = "u@u.com", asunto = "s", mensaje = "m", timestamp = timestamp)

        coEvery { dao.getSolicitudPorTimestamp(timestamp) } returns local
        coEvery { api.deleteSolicitud("RID") } returns Response.success(Unit)
        coEvery { dao.deleteSolicitud(timestamp) } returns 1

        val repo = SolicitudRepositorio(db, api)

        repo.borrarSolicitud(timestamp, "u@u.com")

        coVerify { api.deleteSolicitud("RID") }
        coVerify { dao.deleteSolicitud(timestamp) }
    }

}

package com.example.Perfulandia_APP

import android.util.Log
import com.example.Perfulandia_APP.model.Solicitud
import com.example.Perfulandia_APP.repository.SolicitudRepositorio
import com.example.Perfulandia_APP.viewmodel.SolicitudViewModel
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SolicitudViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var scope: TestScope

    @MockK
    lateinit var repo: SolicitudRepositorio

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        io.mockk.every { Log.d(any<String>(), any<String>()) } returns 0
        io.mockk.every { Log.w(any<String>(), any<String>()) } returns 0
        io.mockk.every { Log.e(any<String>(), any<String>()) } returns 0

        MockKAnnotations.init(this, relaxUnitFun = true)
        scope = TestScope(dispatcher)

        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onXChange actualiza el state form`() {
        val vm = SolicitudViewModel(repo)
        vm.onCorreoChange("a@b.com")
        vm.state.value.form.correo shouldBe "a@b.com"
        vm.onAsuntoChange("as")
        vm.state.value.form.asunto shouldBe "as"
        vm.onMensajeChange("ms")
        vm.state.value.form.mensaje shouldBe "ms"
    }

    @Test
    fun `crearYRecargar - éxito actualiza estado y carga locales`() = runTest {
        coEvery { repo.crearSolicitud(any(), any(), any()) } returns Result.success(null)
        coEvery { repo.obtenerSolicitudesDe(any()) } returns listOf<Solicitud>()

        val vm = SolicitudViewModel(repo)
        vm.onCorreoChange("u@u.com")
        vm.crearYRecargar("u@u.com", "as", "msg")


        advanceUntilIdle()

        vm.state.value.success.shouldBeTrue()
        vm.state.value.submitting shouldBe false
        vm.state.value.localItems shouldBe listOf<Solicitud>()
    }

    @Test
    fun `crearYRecargar - fallo pone generalError`() = runTest {
        coEvery { repo.crearSolicitud(any(), any(), any()) } returns Result.failure(Exception("network"))
        val vm = SolicitudViewModel(repo)
        vm.crearYRecargar("u@u.com", "a", "b")

        advanceUntilIdle()

        vm.state.value.generalError shouldBe "network"
        vm.state.value.submitting shouldBe false
    }

    @Test
    fun `loadSolicitudesLocales - carga items y actualiza submitting`() = runTest {
        val sample = Solicitud(id = 1L, remoteId = null, correoUsuario = "x@x.com", asunto = "a", mensaje = "m", timestamp = 1L)
        coEvery { repo.obtenerSolicitudesDe("x@x.com") } returns listOf(sample)

        val vm = SolicitudViewModel(repo)
        vm.loadSolicitudesLocales("x@x.com")

        advanceUntilIdle()

        vm.state.value.localItems.size shouldBe 1
        vm.state.value.submitting shouldBe false
    }
}

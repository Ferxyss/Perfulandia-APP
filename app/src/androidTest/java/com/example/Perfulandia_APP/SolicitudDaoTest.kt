package com.example.Perfulandia_APP

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.Perfulandia_APP.model.AppDatabase
import com.example.Perfulandia_APP.model.Solicitud
import com.example.Perfulandia_APP.model.SolicitudDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SolicitudDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: SolicitudDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.solicitudDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetByTimestamp() = runBlocking {
        val s = Solicitud(
            remoteId = "r1",
            correoUsuario = "u@u.com",
            asunto = "A",
            mensaje = "M",
            timestamp = 123456L
        )
        dao.insertSolicitud(s)
        val got = dao.getSolicitudPorTimestamp(123456L)
        assertEquals("r1", got?.remoteId)
        assertEquals("A", got?.asunto)
        assertEquals("M", got?.mensaje)
        assertEquals("u@u.com", got?.correoUsuario)
    }

    @Test
    fun insertListAndGetAllOrdered() = runBlocking {
        val first = Solicitud(
            remoteId = "a",
            correoUsuario = "u@u.com",
            asunto = "A",
            mensaje = "M",
            timestamp = 2000L
        )
        val second = Solicitud(
            remoteId = "b",
            correoUsuario = "u@u.com",
            asunto = "B",
            mensaje = "M2",
            timestamp = 1000L
        )
        dao.insertSolicitudes(listOf(first, second))
        val all = dao.getAllSolicitudes()
        assertEquals(2, all.size)
        assertEquals("a", all[0].remoteId)
        assertEquals("b", all[1].remoteId)
    }

    @Test
    fun deleteByUsuarioAndAll() = runBlocking {
        dao.insertSolicitudes(listOf(
            Solicitud(remoteId = "x1", correoUsuario = "x@x.com", asunto = "t", mensaje = "m", timestamp = 1L),
            Solicitud(remoteId = "y1", correoUsuario = "y@y.com", asunto = "t2", mensaje = "m", timestamp = 2L)
        ))


        dao.deleteSolicitudesPorUsuario("x@x.com")
        val forX = dao.getSolicitudesPorUsuario("x@x.com")
        assertEquals(0, forX.size)


        dao.deleteAllSolicitudes()
        val all = dao.getAllSolicitudes()
        assertEquals(0, all.size)
    }

    @Test
    fun deleteSolicitudByTimestamp_returnsRowsAffected() = runBlocking {
        val ts = 5555L
        dao.insertSolicitud(
            Solicitud(remoteId = "del1", correoUsuario = "z@z.com", asunto = "t3", mensaje = "m3", timestamp = ts)
        )
        val rows = dao.deleteSolicitud(ts)
        assertEquals(1, rows)
        val maybe = dao.getSolicitudPorTimestamp(ts)
        assertEquals(null, maybe)
    }

    @Test
    fun getSolicitudByRemoteId_returnsCorrectItem() = runBlocking {
        val ts = 9999L
        val remoteId = "REMID123"
        val s = Solicitud(remoteId = remoteId, correoUsuario = "a@a.com", asunto = "ok", mensaje = "msg", timestamp = ts)
        dao.insertSolicitud(s)
        val found = dao.getSolicitudPorRemoteId(remoteId)
        assertEquals(remoteId, found?.remoteId)
        assertEquals("ok", found?.asunto)
    }
}

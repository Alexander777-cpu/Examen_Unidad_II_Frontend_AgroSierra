package pe.upeu.agrosierra

import pe.upeu.agrosierra.domain.model.Sesion
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SesionRbacTest {

    private val sesionAdmin = Sesion(
        token = "token_admin",
        usuario = "admin",
        nombreCompleto = "Administrador",
        roles = listOf("ADMINISTRADOR"),
        privilegios = setOf("LOTE_LEER", "USUARIO_GESTIONAR")
    )

    private val sesionTecnico = Sesion(
        token = "token_tecnico",
        usuario = "tecnico1",
        nombreCompleto = "Técnico 1",
        roles = listOf("TECNICO"),
        privilegios = setOf("LOTE_LEER", "LABOR_LEER", "LABOR_CREAR", "LABOR_EDITAR")
    )

    private val sesionJefe = Sesion(
        token = "token_jefe",
        usuario = "jefe1",
        nombreCompleto = "Jefe Campo",
        roles = listOf("JEFE_CAMPO"),
        privilegios = setOf("LOTE_LEER", "LABOR_LEER", "LABOR_CREAR", "LABOR_EDITAR", "LABOR_ELIMINAR", "CAMPANIA_CERRAR")
    )

    @Test
    fun testRbacAdministrador() {
        assertTrue(sesionAdmin.puede("LOTE_LEER"))
        assertFalse(sesionAdmin.puede("LABOR_CREAR"))
        assertFalse(sesionAdmin.puede("LABOR_EDITAR"))
        assertFalse(sesionAdmin.puede("LABOR_ELIMINAR"))
        assertFalse(sesionAdmin.puede("CAMPANIA_CERRAR"))
    }

    @Test
    fun testRbacTecnico() {
        assertTrue(sesionTecnico.puede("LOTE_LEER"))
        assertTrue(sesionTecnico.puede("LABOR_CREAR"))
        assertTrue(sesionTecnico.puede("LABOR_EDITAR"))
        assertFalse(sesionTecnico.puede("LABOR_ELIMINAR"))
        assertFalse(sesionTecnico.puede("CAMPANIA_CERRAR"))
    }

    @Test
    fun testRbacJefeCampo() {
        assertTrue(sesionJefe.puede("LOTE_LEER"))
        assertTrue(sesionJefe.puede("LABOR_CREAR"))
        assertTrue(sesionJefe.puede("LABOR_EDITAR"))
        assertTrue(sesionJefe.puede("LABOR_ELIMINAR"))
        assertTrue(sesionJefe.puede("CAMPANIA_CERRAR"))
    }
}

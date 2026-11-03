package pe.upeu.agrosierra

import pe.upeu.agrosierra.domain.model.Labor
import pe.upeu.agrosierra.domain.model.Lote
import pe.upeu.agrosierra.domain.model.ReglasNegocio
import pe.upeu.agrosierra.domain.model.TipoLabor
import kotlin.test.Test
import kotlin.test.assertTrue

class ReglasNegocioTest {

    private val loteAbierto = Lote(
        id = 1L,
        codigo = "L-01",
        nombre = "Lote San José",
        hectareas = 10.0,
        cultivo = "Papa",
        valle = "Mantaro",
        campaniaCerrada = false,
        dosisMaximaPorHectarea = 5.0
    )

    private val loteCerrado = loteAbierto.copy(campaniaCerrada = true)

    @Test
    fun testRN01_RechazarLaborSiCampaniaCerrada() {
        val result = ReglasNegocio.validarRegistroLabor(loteCerrado, TipoLabor.RIEGO, 10.0)
        assertTrue(result.isFailure)
    }

    @Test
    fun testRN03_RechazarCierreCampaniaSinCosecha() {
        val labores = listOf(
            Labor(id = 1L, loteId = 1L, tipo = TipoLabor.RIEGO, fecha = "2026-11-01", responsable = "Juan", cantidad = 10.0, observacion = "")
        )
        val result = ReglasNegocio.validarCierreCampania(labores)
        assertTrue(result.isFailure)
    }

    @Test
    fun testRN04_RechazarFumigacionExcediendoDosisMaxima() {
        val result = ReglasNegocio.validarRegistroLabor(loteAbierto, TipoLabor.FUMIGACION, 60.0)
        assertTrue(result.isFailure)
    }
}

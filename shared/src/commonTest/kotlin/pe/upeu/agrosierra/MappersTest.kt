package pe.upeu.agrosierra

import pe.upeu.agrosierra.data.remote.dto.LaborResponseDto
import pe.upeu.agrosierra.data.remote.dto.LoteResponseDto
import pe.upeu.agrosierra.data.remote.dto.toDomain
import pe.upeu.agrosierra.domain.model.TipoLabor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MappersTest {

    @Test
    fun testLoteResponseDtoToDomain() {
        val dto = LoteResponseDto(
            id = 5L,
            codigo = "L-05",
            nombre = "Lote Vilcanota",
            hectareas = 15.5,
            cultivo = "Maíz",
            valle = "Sagrado",
            campaniaCerrada = false,
            dosisMaximaPorHectarea = 8.0
        )
        val domain = dto.toDomain()

        assertEquals(5L, domain.id)
        assertEquals("L-05", domain.codigo)
        assertEquals("Lote Vilcanota", domain.nombre)
        assertEquals(15.5, domain.hectareas)
        assertEquals("Maíz", domain.cultivo)
        assertEquals("Sagrado", domain.valle)
        assertFalse(domain.campaniaCerrada)
        assertEquals(8.0, domain.dosisMaximaPorHectarea)
    }

    @Test
    fun testLaborResponseDtoToDomain() {
        val dto = LaborResponseDto(
            id = 12L,
            loteId = 5L,
            tipo = "FUMIGACION",
            fecha = "2026-11-03",
            responsable = "Carlos",
            cantidad = 25.0,
            observacion = "Aplicado en la mañana"
        )
        val domain = dto.toDomain()

        assertEquals(12L, domain.id)
        assertEquals(5L, domain.loteId)
        assertEquals(TipoLabor.FUMIGACION, domain.tipo)
        assertEquals("2026-11-03", domain.fecha)
        assertEquals("Carlos", domain.responsable)
        assertEquals(25.0, domain.cantidad)
        assertEquals("Aplicado en la mañana", domain.observacion)
    }
}

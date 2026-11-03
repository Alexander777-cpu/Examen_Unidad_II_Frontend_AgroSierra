package pe.upeu.agrosierra.data.remote.dto

import kotlinx.serialization.Serializable
import pe.upeu.agrosierra.domain.model.Labor
import pe.upeu.agrosierra.domain.model.TipoLabor

@Serializable
data class LaborResponseDto(
    val id: Long,
    val loteId: Long,
    val loteCodigo: String? = null,
    val loteNombre: String? = null,
    val tipo: String,
    val fecha: String,
    val responsable: String = "",
    val cantidad: Double,
    val observacion: String? = null,
    val fechaRegistro: String? = null
)

fun LaborResponseDto.toDomain(): Labor {
    val tipoLaborEnum = try {
        TipoLabor.valueOf(tipo.uppercase())
    } catch (_: Exception) {
        TipoLabor.RIEGO
    }
    return Labor(
        id = id,
        loteId = loteId,
        tipo = tipoLaborEnum,
        fecha = fecha,
        responsable = responsable,
        cantidad = cantidad,
        observacion = observacion ?: "",
        pendiente = false
    )
}

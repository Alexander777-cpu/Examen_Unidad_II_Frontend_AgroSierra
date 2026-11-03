package pe.upeu.agrosierra.data.remote.dto

import kotlinx.serialization.Serializable
import pe.upeu.agrosierra.domain.model.Lote

@Serializable
data class LoteResponseDto(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val hectareas: Double,
    val cultivo: String,
    val valle: String,
    val campaniaCerrada: Boolean = false,
    val dosisMaximaPorHectarea: Double = 0.0
)

fun LoteResponseDto.toDomain(): Lote {
    return Lote(
        id = id,
        codigo = codigo,
        nombre = nombre,
        hectareas = hectareas,
        cultivo = cultivo,
        valle = valle,
        campaniaCerrada = campaniaCerrada,
        dosisMaximaPorHectarea = dosisMaximaPorHectarea
    )
}

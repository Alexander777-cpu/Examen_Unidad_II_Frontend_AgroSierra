package pe.upeu.agrosierra.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LaborRequestDto(
    val loteId: Long,
    val tipo: String,
    val fecha: String,
    val responsable: String? = null,
    val cantidad: Double,
    val observacion: String? = null
)

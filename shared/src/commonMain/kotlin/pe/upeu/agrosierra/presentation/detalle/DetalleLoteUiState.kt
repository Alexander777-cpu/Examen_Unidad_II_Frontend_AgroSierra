package pe.upeu.agrosierra.presentation.detalle

import pe.upeu.agrosierra.domain.model.Labor
import pe.upeu.agrosierra.domain.model.Lote
import pe.upeu.agrosierra.domain.model.Sesion

data class DetalleLoteUiState(
    val lote: Lote? = null,
    val labores: List<Labor> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val sesion: Sesion? = null,
    val mensajeExito: String? = null,
    val sincronizando: Boolean = false
)

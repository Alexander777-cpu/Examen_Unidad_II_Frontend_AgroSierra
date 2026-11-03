package pe.upeu.agrosierra.presentation.padron

import pe.upeu.agrosierra.domain.model.Lote
import pe.upeu.agrosierra.domain.model.Sesion

data class LotesUiState(
    val lotes: List<Lote> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val valleFiltro: String? = null,
    val sesion: Sesion? = null,
    val sincronizando: Boolean = false,
    val mensajeSync: String? = null
)

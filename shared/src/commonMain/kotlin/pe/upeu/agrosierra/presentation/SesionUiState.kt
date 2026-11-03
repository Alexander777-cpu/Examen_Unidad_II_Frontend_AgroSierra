package pe.upeu.agrosierra.presentation

import pe.upeu.agrosierra.domain.model.Sesion

data class SesionUiState(
    val usuario: String = "",
    val clave: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val sesion: Sesion? = null
)

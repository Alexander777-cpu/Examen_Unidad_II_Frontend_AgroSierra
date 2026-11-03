package pe.upeu.agrosierra.presentation.padron

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.agrosierra.domain.model.Lote
import pe.upeu.agrosierra.domain.repository.AuthRepository
import pe.upeu.agrosierra.domain.repository.LoteRepository

class LotesViewModel(
    private val loteRepository: LoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LotesUiState())
    val uiState: StateFlow<LotesUiState> = _uiState.asStateFlow()

    private var lotesOriginales: List<Lote> = emptyList()

    init {
        cargarSesionYFiltro(null)
    }

    fun cargarSesionYFiltro(valle: String?) {
        viewModelScope.launch {
            val sesion = authRepository.obtenerSesion()
            _uiState.update { it.copy(sesion = sesion, valleFiltro = valle, cargando = true, error = null) }
            val result = loteRepository.listarLotes(valle = null)
            result.onSuccess { lista ->
                lotesOriginales = lista
                aplicarFiltro(valle)
            }.onFailure { throwable ->
                _uiState.update { it.copy(cargando = false, error = throwable.message ?: "Error al cargar lotes") }
            }
        }
    }

    fun filtrarPorValle(valle: String?) {
        _uiState.update { it.copy(valleFiltro = valle) }
        aplicarFiltro(valle)
    }

    private fun aplicarFiltro(valle: String?) {
        val filtroSeleccionado = valle ?: ""
        val lotesFiltrados = if (filtroSeleccionado == "Todos" || filtroSeleccionado.isBlank()) {
            lotesOriginales
        } else {
            lotesOriginales.filter { lote ->
                lote.valle.contains(filtroSeleccionado, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(cargando = false, lotes = lotesFiltrados, error = null) }
    }

    fun cerrarSesion(onCerrado: () -> Unit) {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            onCerrado()
        }
    }
}

package pe.upeu.agrosierra.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.agrosierra.domain.repository.AuthRepository
import pe.upeu.agrosierra.domain.repository.LaborRepository
import pe.upeu.agrosierra.domain.repository.LoteRepository

class DetalleLoteViewModel(
    private val loteId: Long,
    private val loteRepository: LoteRepository,
    private val laborRepository: LaborRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleLoteUiState())
    val uiState: StateFlow<DetalleLoteUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            val sesion = authRepository.obtenerSesion()
            _uiState.update { it.copy(sesion = sesion) }

            val loteResult = loteRepository.obtenerLote(loteId)
            val laboresResult = laborRepository.listarLaboresPorLote(loteId)

            loteResult.onSuccess { loteObj ->
                _uiState.update { it.copy(lote = loteObj) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(error = throwable.message ?: "Error al obtener lote") }
            }

            laboresResult.onSuccess { listaLabores ->
                _uiState.update { it.copy(cargando = false, labores = listaLabores) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(cargando = false, error = throwable.message ?: "Error al cargar labores") }
            }
        }
    }

    fun cerrarCampania() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            loteRepository.cerrarCampania(loteId)
                .onSuccess { loteActualizado ->
                    _uiState.update { it.copy(cargando = false, lote = loteActualizado, mensajeExito = "Campaña cerrada con éxito") }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(cargando = false, error = throwable.message ?: "Error al cerrar campaña") }
                }
        }
    }

    fun eliminarLabor(laborId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            laborRepository.eliminarLabor(laborId)
                .onSuccess {
                    _uiState.update { it.copy(mensajeExito = "Labor eliminada con éxito") }
                    cargarDatos()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(cargando = false, error = throwable.message ?: "Error al eliminar labor") }
                }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeExito = null, error = null) }
    }
}

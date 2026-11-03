package pe.upeu.agrosierra.presentation.labor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.agrosierra.data.remote.ErrorApiException
import pe.upeu.agrosierra.domain.model.ErrorApi
import pe.upeu.agrosierra.domain.model.TipoLabor
import pe.upeu.agrosierra.domain.repository.LaborRepository
import pe.upeu.agrosierra.domain.repository.LoteRepository

class FormularioLaborViewModel(
    private val loteId: Long,
    private val laborId: Long?,
    private val loteRepository: LoteRepository,
    private val laborRepository: LaborRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioLaborUiState(loteId = loteId, laborId = laborId))
    val uiState: StateFlow<FormularioLaborUiState> = _uiState.asStateFlow()

    init {
        cargarInicial()
    }

    private fun cargarInicial() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            loteRepository.obtenerLote(loteId).onSuccess { loteObj ->
                _uiState.update { it.copy(lote = loteObj) }
            }

            if (laborId != null) {
                laborRepository.listarLaboresPorLote(loteId).onSuccess { labores ->
                    val laborEncontrada = labores.find { it.id == laborId }
                    if (laborEncontrada != null) {
                        _uiState.update {
                            it.copy(
                                tipo = laborEncontrada.tipo,
                                fecha = laborEncontrada.fecha,
                                responsable = laborEncontrada.responsable,
                                cantidad = laborEncontrada.cantidad.toString(),
                                observacion = laborEncontrada.observacion
                            )
                        }
                    }
                }
            }
            _uiState.update { it.copy(cargando = false) }
        }
    }

    fun onTipoChange(tipo: TipoLabor) {
        _uiState.update { it.copy(tipo = tipo, erroresCampo = it.erroresCampo - "tipo") }
    }

    fun onFechaChange(fecha: String) {
        _uiState.update { it.copy(fecha = fecha, erroresCampo = it.erroresCampo - "fecha") }
    }

    fun onResponsableChange(responsable: String) {
        _uiState.update { it.copy(responsable = responsable) }
    }

    fun onCantidadChange(cantidad: String) {
        _uiState.update { it.copy(cantidad = cantidad, erroresCampo = it.erroresCampo - "cantidad") }
    }

    fun onObservacionChange(observacion: String) {
        _uiState.update { it.copy(observacion = observacion) }
    }

    fun guardar() {
        val state = _uiState.value
        val cantidadDouble = state.cantidad.toDoubleOrNull()

        if (state.fecha.isBlank()) {
            _uiState.update { it.copy(erroresCampo = it.erroresCampo + ("fecha" to "La fecha es obligatoria")) }
            return
        }

        if (cantidadDouble == null || cantidadDouble <= 0) {
            _uiState.update { it.copy(erroresCampo = it.erroresCampo + ("cantidad" to "Ingrese una cantidad válida mayor a 0")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null, erroresCampo = emptyMap()) }

            val result = if (laborId == null) {
                laborRepository.registrarLabor(
                    loteId = loteId,
                    tipo = state.tipo,
                    fecha = state.fecha,
                    responsable = state.responsable.ifBlank { null },
                    cantidad = cantidadDouble,
                    observacion = state.observacion.ifBlank { null }
                )
            } else {
                laborRepository.corregirLabor(
                    id = laborId,
                    loteId = loteId,
                    tipo = state.tipo,
                    fecha = state.fecha,
                    responsable = state.responsable.ifBlank { null },
                    cantidad = cantidadDouble,
                    observacion = state.observacion.ifBlank { null }
                )
            }

            result.onSuccess {
                _uiState.update { it.copy(cargando = false, guardadoExitoso = true) }
            }.onFailure { throwable ->
                val errorState = when (throwable) {
                    is ErrorApiException -> when (val apiError = throwable.errorApi) {
                        is ErrorApi.Validacion -> {
                            state.copy(cargando = false, erroresCampo = apiError.porCampo)
                        }
                        is ErrorApi.Conflicto -> {
                            state.copy(cargando = false, errorGeneral = apiError.mensaje)
                        }
                        else -> {
                            state.copy(cargando = false, errorGeneral = throwable.message ?: "Error al guardar labor")
                        }
                    }
                    else -> state.copy(cargando = false, errorGeneral = throwable.message ?: "Error desconocido")
                }
                _uiState.value = errorState
            }
        }
    }
}

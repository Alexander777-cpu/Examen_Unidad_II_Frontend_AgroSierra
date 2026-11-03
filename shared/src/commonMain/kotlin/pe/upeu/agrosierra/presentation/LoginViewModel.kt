package pe.upeu.agrosierra.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.agrosierra.data.remote.ErrorApiException
import pe.upeu.agrosierra.domain.model.ErrorApi
import pe.upeu.agrosierra.domain.repository.AuthRepository

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    init {
        verificarSesionExistente()
    }

    fun onUsuarioChange(nuevoUsuario: String) {
        _uiState.update { it.copy(usuario = nuevoUsuario, error = null) }
    }

    fun onClaveChange(nuevaClave: String) {
        _uiState.update { it.copy(clave = nuevaClave, error = null) }
    }

    fun verificarSesionExistente() {
        viewModelScope.launch {
            val sesion = authRepository.obtenerSesion()
            if (sesion != null) {
                _uiState.update { it.copy(sesion = sesion) }
            }
        }
    }

    fun login() {
        val state = _uiState.value
        if (state.usuario.isBlank() || state.clave.isBlank()) {
            _uiState.update { it.copy(error = "Por favor ingrese usuario y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            val result = authRepository.login(state.usuario, state.clave)
            result.onSuccess { sesion ->
                _uiState.update { it.copy(cargando = false, sesion = sesion, error = null) }
            }.onFailure { throwable ->
                val mensaje = when (throwable) {
                    is ErrorApiException -> when (val apiError = throwable.errorApi) {
                        is ErrorApi.NoAutorizado -> "Credenciales incorrectas"
                        is ErrorApi.SinPermiso -> "No tiene permisos para realizar esta acción"
                        is ErrorApi.Validacion -> apiError.porCampo.values.firstOrNull() ?: "Error de validación"
                        is ErrorApi.Conflicto -> apiError.mensaje
                        is ErrorApi.NoEncontrado -> "Recurso no encontrado"
                        is ErrorApi.Servidor -> "Error interno del servidor"
                        is ErrorApi.SinConexion -> "Sin conexión al servidor"
                        is ErrorApi.TiempoAgotado -> "Tiempo de espera agotado"
                    }
                    else -> throwable.message ?: "Error al iniciar sesión"
                }
                _uiState.update { it.copy(cargando = false, error = mensaje) }
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            _uiState.update { SesionUiState() }
        }
    }
}

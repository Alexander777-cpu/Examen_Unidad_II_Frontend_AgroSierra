package pe.upeu.agrosierra

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import pe.upeu.agrosierra.presentation.LoginScreen
import pe.upeu.agrosierra.presentation.LoginViewModel
import pe.upeu.agrosierra.presentation.PadronLotesScreen

@Composable
fun App(
    viewModel: LoginViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val sesion = uiState.sesion
            if (sesion != null) {
                PadronLotesScreen(
                    usuario = sesion.nombreCompleto.ifBlank { sesion.usuario },
                    onCerrarSesion = viewModel::cerrarSesion
                )
            } else {
                LoginScreen(viewModel = viewModel)
            }
        }
    }
}

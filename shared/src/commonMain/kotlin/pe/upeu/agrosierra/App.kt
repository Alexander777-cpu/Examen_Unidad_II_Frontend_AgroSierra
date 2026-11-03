package pe.upeu.agrosierra

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import pe.upeu.agrosierra.presentation.LoginScreen
import pe.upeu.agrosierra.presentation.LoginViewModel
import pe.upeu.agrosierra.presentation.detalle.DetalleLoteScreen
import pe.upeu.agrosierra.presentation.detalle.DetalleLoteViewModel
import pe.upeu.agrosierra.presentation.labor.FormularioLaborScreen
import pe.upeu.agrosierra.presentation.labor.FormularioLaborViewModel
import pe.upeu.agrosierra.presentation.navigation.Screen
import pe.upeu.agrosierra.presentation.padron.LotesScreen
import pe.upeu.agrosierra.presentation.padron.LotesViewModel

@Composable
fun App(
    loginViewModel: LoginViewModel = koinInject()
) {
    val loginUiState by loginViewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    // Si hay sesión activa al arrancar y estamos en Login, redirigir a PadronLotes
    if (loginUiState.sesion != null && currentScreen is Screen.Login) {
        currentScreen = Screen.PadronLotes
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (val screen = currentScreen) {
                is Screen.Login -> {
                    LoginScreen(viewModel = loginViewModel)
                }
                is Screen.PadronLotes -> {
                    val lotesViewModel: LotesViewModel = koinInject()
                    LotesScreen(
                        viewModel = lotesViewModel,
                        onLoteClick = { loteId ->
                            currentScreen = Screen.DetalleLote(loteId)
                        },
                        onCerrarSesion = {
                            loginViewModel.cerrarSesion()
                            currentScreen = Screen.Login
                        }
                    )
                }
                is Screen.DetalleLote -> {
                    val detalleViewModel: DetalleLoteViewModel = koinInject { parametersOf(screen.loteId) }
                    DetalleLoteScreen(
                        viewModel = detalleViewModel,
                        onVolver = {
                            currentScreen = Screen.PadronLotes
                        },
                        onRegistrarLabor = { loteId ->
                            currentScreen = Screen.FormularioLabor(loteId = loteId, laborId = null)
                        },
                        onEditarLabor = { loteId, laborId ->
                            currentScreen = Screen.FormularioLabor(loteId = loteId, laborId = laborId)
                        }
                    )
                }
                is Screen.FormularioLabor -> {
                    val formularioViewModel: FormularioLaborViewModel = koinInject { parametersOf(screen.loteId, screen.laborId) }
                    FormularioLaborScreen(
                        viewModel = formularioViewModel,
                        onVolver = {
                            currentScreen = Screen.DetalleLote(screen.loteId)
                        }
                    )
                }
            }
        }
    }
}

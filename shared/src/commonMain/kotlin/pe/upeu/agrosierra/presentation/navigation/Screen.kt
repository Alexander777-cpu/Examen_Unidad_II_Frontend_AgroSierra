package pe.upeu.agrosierra.presentation.navigation

sealed interface Screen {
    data object Login : Screen
    data object PadronLotes : Screen
    data class DetalleLote(val loteId: Long) : Screen
    data class FormularioLabor(val loteId: Long, val laborId: Long? = null) : Screen
}

package pe.upeu.agrosierra.presentation.labor

import pe.upeu.agrosierra.domain.model.Labor
import pe.upeu.agrosierra.domain.model.Lote
import pe.upeu.agrosierra.domain.model.TipoLabor

data class FormularioLaborUiState(
    val loteId: Long = 0L,
    val laborId: Long? = null,
    val lote: Lote? = null,
    val tipo: TipoLabor = TipoLabor.RIEGO,
    val fecha: String = "",
    val responsable: String = "",
    val cantidad: String = "",
    val observacion: String = "",
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val erroresCampo: Map<String, String> = emptyMap(),
    val guardadoExitoso: Boolean = false
)

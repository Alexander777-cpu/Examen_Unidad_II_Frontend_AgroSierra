package pe.upeu.agrosierra.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.upeu.agrosierra.domain.model.Labor
import pe.upeu.agrosierra.domain.model.TipoLabor

interface LaborRepository {
    fun observarLaboresPorLote(loteId: Long): Flow<List<Labor>>
    suspend fun refrescarLaboresPorLote(loteId: Long): Result<List<Labor>>
    suspend fun listarLaboresPorLote(loteId: Long, pagina: Int = 0, tamanio: Int = 20): Result<List<Labor>>
    suspend fun registrarLabor(
        loteId: Long,
        tipo: TipoLabor,
        fecha: String,
        responsable: String?,
        cantidad: Double,
        observacion: String?
    ): Result<Labor>
    suspend fun corregirLabor(
        id: Long,
        loteId: Long,
        tipo: TipoLabor,
        fecha: String,
        responsable: String?,
        cantidad: Double,
        observacion: String?
    ): Result<Labor>
    suspend fun eliminarLabor(id: Long): Result<Unit>
    suspend fun obtenerPendientes(): List<Labor>
}

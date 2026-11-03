package pe.upeu.agrosierra.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.upeu.agrosierra.domain.model.Lote

interface LoteRepository {
    fun observarLotes(valle: String? = null): Flow<List<Lote>>
    suspend fun refrescarLotes(valle: String? = null): Result<List<Lote>>
    suspend fun listarLotes(valle: String? = null, pagina: Int = 0, tamanio: Int = 20): Result<List<Lote>>
    suspend fun obtenerLote(id: Long): Result<Lote>
    suspend fun cerrarCampania(id: Long): Result<Lote>
}

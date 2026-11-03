package pe.upeu.agrosierra.domain.repository

import pe.upeu.agrosierra.domain.model.Lote

interface LoteRepository {
    suspend fun listarLotes(valle: String? = null, pagina: Int = 0, tamanio: Int = 20): Result<List<Lote>>
    suspend fun obtenerLote(id: Long): Result<Lote>
    suspend fun cerrarCampania(id: Long): Result<Lote>
}

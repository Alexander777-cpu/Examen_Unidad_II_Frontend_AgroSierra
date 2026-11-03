package pe.upeu.agrosierra.data.repository

import pe.upeu.agrosierra.data.remote.LoteApi
import pe.upeu.agrosierra.data.remote.dto.toDomain
import pe.upeu.agrosierra.domain.model.Lote
import pe.upeu.agrosierra.domain.repository.LoteRepository

class LoteRepositoryRest(
    private val loteApi: LoteApi
) : LoteRepository {
    override suspend fun listarLotes(valle: String?, pagina: Int, tamanio: Int): Result<List<Lote>> {
        return loteApi.listar(valle, pagina, tamanio).map { paginaDto ->
            paginaDto.contenido.map { it.toDomain() }
        }
    }

    override suspend fun obtenerLote(id: Long): Result<Lote> {
        return loteApi.obtener(id).map { it.toDomain() }
    }

    override suspend fun cerrarCampania(id: Long): Result<Lote> {
        return loteApi.cerrarCampania(id).map { it.toDomain() }
    }
}

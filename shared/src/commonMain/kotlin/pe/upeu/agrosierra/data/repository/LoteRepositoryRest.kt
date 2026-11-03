package pe.upeu.agrosierra.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.agrosierra.data.remote.LoteApi
import pe.upeu.agrosierra.data.remote.dto.toDomain
import pe.upeu.agrosierra.db.AgroDb
import pe.upeu.agrosierra.db.LoteEntity
import pe.upeu.agrosierra.domain.model.Lote
import pe.upeu.agrosierra.domain.repository.LoteRepository

class LoteRepositoryRest(
    private val loteApi: LoteApi,
    private val db: AgroDb
) : LoteRepository {

    override fun observarLotes(valle: String?): Flow<List<Lote>> {
        return db.agroDbQueries.selectAllLotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                val domainList = entities.map { it.toDomain() }
                if (valle.isNullOrBlank() || valle == "Todos") {
                    domainList
                } else {
                    domainList.filter { it.valle.contains(valle, ignoreCase = true) }
                }
            }
    }

    override suspend fun refrescarLotes(valle: String?): Result<List<Lote>> {
        val result = loteApi.listar(valle)
        return result.map { paginaDto ->
            val domainList = paginaDto.contenido.map { it.toDomain() }
            db.transaction {
                domainList.forEach { lote ->
                    db.agroDbQueries.insertLote(
                        id = lote.id,
                        codigo = lote.codigo,
                        nombre = lote.nombre,
                        hectareas = lote.hectareas,
                        cultivo = lote.cultivo,
                        valle = lote.valle,
                        campaniaCerrada = if (lote.campaniaCerrada) 1L else 0L,
                        dosisMaximaPorHectarea = lote.dosisMaximaPorHectarea
                    )
                }
            }
            domainList
        }
    }

    override suspend fun listarLotes(valle: String?, pagina: Int, tamanio: Int): Result<List<Lote>> {
        val remoteResult = refrescarLotes(valle)
        if (remoteResult.isSuccess) {
            return remoteResult
        }
        val localEntities = db.agroDbQueries.selectAllLotes().executeAsList()
        val domainList = localEntities.map { it.toDomain() }
        val filtered = if (valle.isNullOrBlank() || valle == "Todos") {
            domainList
        } else {
            domainList.filter { it.valle.contains(valle, ignoreCase = true) }
        }
        return Result.success(filtered)
    }

    override suspend fun obtenerLote(id: Long): Result<Lote> {
        val remoteResult = loteApi.obtener(id)
        if (remoteResult.isSuccess) {
            val lote = remoteResult.getOrThrow().toDomain()
            db.agroDbQueries.insertLote(
                id = lote.id,
                codigo = lote.codigo,
                nombre = lote.nombre,
                hectareas = lote.hectareas,
                cultivo = lote.cultivo,
                valle = lote.valle,
                campaniaCerrada = if (lote.campaniaCerrada) 1L else 0L,
                dosisMaximaPorHectarea = lote.dosisMaximaPorHectarea
            )
            return Result.success(lote)
        }
        val localEntity = db.agroDbQueries.selectLoteById(id).executeAsOneOrNull()
        return if (localEntity != null) {
            Result.success(localEntity.toDomain())
        } else {
            remoteResult.map { it.toDomain() }
        }
    }

    override suspend fun cerrarCampania(id: Long): Result<Lote> {
        val result = loteApi.cerrarCampania(id)
        return result.map { dto ->
            val lote = dto.toDomain()
            db.agroDbQueries.insertLote(
                id = lote.id,
                codigo = lote.codigo,
                nombre = lote.nombre,
                hectareas = lote.hectareas,
                cultivo = lote.cultivo,
                valle = lote.valle,
                campaniaCerrada = 1L,
                dosisMaximaPorHectarea = lote.dosisMaximaPorHectarea
            )
            lote
        }
    }
}

fun LoteEntity.toDomain() = Lote(
    id = id,
    codigo = codigo,
    nombre = nombre,
    hectareas = hectareas,
    cultivo = cultivo,
    valle = valle,
    campaniaCerrada = campaniaCerrada == 1L,
    dosisMaximaPorHectarea = dosisMaximaPorHectarea
)

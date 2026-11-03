package pe.upeu.agrosierra.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.agrosierra.data.remote.ErrorApiException
import pe.upeu.agrosierra.data.remote.LaborApi
import pe.upeu.agrosierra.data.remote.dto.LaborRequestDto
import pe.upeu.agrosierra.data.remote.dto.toDomain
import pe.upeu.agrosierra.db.AgroDb
import pe.upeu.agrosierra.db.LaborEntity
import pe.upeu.agrosierra.domain.model.ErrorApi
import pe.upeu.agrosierra.domain.model.Labor
import pe.upeu.agrosierra.domain.model.TipoLabor
import pe.upeu.agrosierra.domain.repository.LaborRepository

class LaborRepositoryRest(
    private val laborApi: LaborApi,
    private val db: AgroDb
) : LaborRepository {

    override fun observarLaboresPorLote(loteId: Long): Flow<List<Labor>> {
        return db.agroDbQueries.selectAllLaboresByLote(loteId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun refrescarLaboresPorLote(loteId: Long): Result<List<Labor>> {
        val result = laborApi.listarPorLote(loteId)
        return result.map { paginaDto ->
            val domainList = paginaDto.contenido.map { it.toDomain() }
            db.transaction {
                domainList.forEach { labor ->
                    db.agroDbQueries.insertLabor(
                        id = null,
                        servidorId = labor.id,
                        loteId = labor.loteId,
                        tipo = labor.tipo.name,
                        fecha = labor.fecha,
                        responsable = labor.responsable,
                        cantidad = labor.cantidad,
                        observacion = labor.observacion,
                        sincronizado = 1L
                    )
                }
            }
            domainList
        }
    }

    override suspend fun listarLaboresPorLote(loteId: Long, pagina: Int, tamanio: Int): Result<List<Labor>> {
        val remoteResult = refrescarLaboresPorLote(loteId)
        if (remoteResult.isSuccess) {
            return remoteResult
        }
        val localEntities = db.agroDbQueries.selectAllLaboresByLote(loteId).executeAsList()
        val domainList = localEntities.map { it.toDomain() }
        return Result.success(domainList)
    }

    override suspend fun registrarLabor(
        loteId: Long,
        tipo: TipoLabor,
        fecha: String,
        responsable: String?,
        cantidad: Double,
        observacion: String?
    ): Result<Labor> {
        val request = LaborRequestDto(
            loteId = loteId,
            tipo = tipo.name,
            fecha = fecha,
            responsable = responsable,
            cantidad = cantidad,
            observacion = observacion
        )

        val remoteResult = laborApi.registrar(request)
        return if (remoteResult.isSuccess) {
            val remoteLabor = remoteResult.getOrThrow().toDomain()
            db.agroDbQueries.insertLabor(
                id = null,
                servidorId = remoteLabor.id,
                loteId = remoteLabor.loteId,
                tipo = remoteLabor.tipo.name,
                fecha = remoteLabor.fecha,
                responsable = remoteLabor.responsable,
                cantidad = remoteLabor.cantidad,
                observacion = remoteLabor.observacion,
                sincronizado = 1L
            )
            Result.success(remoteLabor)
        } else {
            val exception = remoteResult.exceptionOrNull()
            if (exception is ErrorApiException && exception.errorApi is ErrorApi.SinConexion) {
                db.agroDbQueries.insertLabor(
                    id = null,
                    servidorId = null,
                    loteId = loteId,
                    tipo = tipo.name,
                    fecha = fecha,
                    responsable = responsable ?: "",
                    cantidad = cantidad,
                    observacion = observacion ?: "",
                    sincronizado = 0L
                )
                val laborLocal = Labor(
                    id = 0L,
                    servidorId = null,
                    loteId = loteId,
                    tipo = tipo,
                    fecha = fecha,
                    responsable = responsable ?: "",
                    cantidad = cantidad,
                    observacion = observacion ?: "",
                    pendiente = true,
                    sincronizado = false
                )
                Result.success(laborLocal)
            } else {
                Result.failure(exception ?: Exception("Error al registrar labor"))
            }
        }
    }

    override suspend fun corregirLabor(
        id: Long,
        loteId: Long,
        tipo: TipoLabor,
        fecha: String,
        responsable: String?,
        cantidad: Double,
        observacion: String?
    ): Result<Labor> {
        val request = LaborRequestDto(
            loteId = loteId,
            tipo = tipo.name,
            fecha = fecha,
            responsable = responsable,
            cantidad = cantidad,
            observacion = observacion
        )
        val result = laborApi.corregir(id, request)
        return result.map { dto ->
            val labor = dto.toDomain()
            db.agroDbQueries.insertLabor(
                id = null,
                servidorId = labor.id,
                loteId = labor.loteId,
                tipo = labor.tipo.name,
                fecha = labor.fecha,
                responsable = labor.responsable,
                cantidad = labor.cantidad,
                observacion = labor.observacion,
                sincronizado = 1L
            )
            labor
        }
    }

    override suspend fun eliminarLabor(id: Long): Result<Unit> {
        val result = laborApi.eliminar(id)
        if (result.isSuccess) {
            db.agroDbQueries.deleteLaborLocal(id, id)
        }
        return result
    }

    override suspend fun obtenerPendientes(): List<Labor> {
        val entities = db.agroDbQueries.selectLaboresPendientes().executeAsList()
        return entities.map { it.toDomain() }
    }
}

fun LaborEntity.toDomain(): Labor {
    val tipoLaborEnum = try {
        TipoLabor.valueOf(tipo.uppercase())
    } catch (_: Exception) {
        TipoLabor.RIEGO
    }
    return Labor(
        id = id,
        servidorId = servidorId,
        loteId = loteId,
        tipo = tipoLaborEnum,
        fecha = fecha,
        responsable = responsable,
        cantidad = cantidad,
        observacion = observacion ?: "",
        pendiente = sincronizado == 0L,
        sincronizado = sincronizado == 1L
    )
}

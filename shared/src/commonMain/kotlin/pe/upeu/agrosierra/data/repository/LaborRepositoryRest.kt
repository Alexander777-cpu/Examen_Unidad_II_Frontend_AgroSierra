package pe.upeu.agrosierra.data.repository

import pe.upeu.agrosierra.data.remote.LaborApi
import pe.upeu.agrosierra.data.remote.dto.LaborRequestDto
import pe.upeu.agrosierra.data.remote.dto.toDomain
import pe.upeu.agrosierra.domain.model.Labor
import pe.upeu.agrosierra.domain.model.TipoLabor
import pe.upeu.agrosierra.domain.repository.LaborRepository

class LaborRepositoryRest(
    private val laborApi: LaborApi
) : LaborRepository {
    override suspend fun listarLaboresPorLote(loteId: Long, pagina: Int, tamanio: Int): Result<List<Labor>> {
        return laborApi.listarPorLote(loteId, pagina, tamanio).map { paginaDto ->
            paginaDto.contenido.map { it.toDomain() }
        }
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
        return laborApi.registrar(request).map { it.toDomain() }
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
        return laborApi.corregir(id, request).map { it.toDomain() }
    }

    override suspend fun eliminarLabor(id: Long): Result<Unit> {
        return laborApi.eliminar(id)
    }
}

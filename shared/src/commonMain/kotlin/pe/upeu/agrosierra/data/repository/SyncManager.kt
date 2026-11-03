package pe.upeu.agrosierra.data.repository

import pe.upeu.agrosierra.data.remote.LaborApi
import pe.upeu.agrosierra.data.remote.dto.LaborRequestDto
import pe.upeu.agrosierra.db.AgroDb
import pe.upeu.agrosierra.domain.repository.LaborRepository

class SyncManager(
    private val laborRepository: LaborRepository,
    private val laborApi: LaborApi,
    private val db: AgroDb
) {
    suspend fun sincronizarPendientes(): Result<Int> {
        val pendientes = laborRepository.obtenerPendientes()
        var exitoCount = 0

        for (labor in pendientes) {
            val request = LaborRequestDto(
                loteId = labor.loteId,
                tipo = labor.tipo.name,
                fecha = labor.fecha,
                responsable = labor.responsable,
                cantidad = labor.cantidad,
                observacion = labor.observacion
            )
            val result = laborApi.registrar(request)
            result.onSuccess { remoteDto ->
                db.agroDbQueries.updateLaborServidorId(
                    servidorId = remoteDto.id,
                    id = labor.id
                )
                exitoCount++
            }
        }
        return Result.success(exitoCount)
    }
}

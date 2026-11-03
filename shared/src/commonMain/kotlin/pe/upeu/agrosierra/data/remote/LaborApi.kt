package pe.upeu.agrosierra.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.upeu.agrosierra.data.remote.dto.LaborRequestDto
import pe.upeu.agrosierra.data.remote.dto.LaborResponseDto
import pe.upeu.agrosierra.data.remote.dto.PaginaResponseDto

class LaborApi(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    suspend fun listarPorLote(loteId: Long, pagina: Int = 0, tamanio: Int = 20): Result<PaginaResponseDto<LaborResponseDto>> {
        return ejecutarLlamada {
            httpClient.get("${baseUrl}lotes/$loteId/labores") {
                parameter("pagina", pagina)
                parameter("tamanio", tamanio)
            }
        }
    }

    suspend fun registrar(request: LaborRequestDto): Result<LaborResponseDto> {
        return ejecutarLlamada {
            httpClient.post("${baseUrl}labores") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun corregir(id: Long, request: LaborRequestDto): Result<LaborResponseDto> {
        return ejecutarLlamada {
            httpClient.put("${baseUrl}labores/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun eliminar(id: Long): Result<Unit> {
        return ejecutarLlamada<Unit> {
            httpClient.delete("${baseUrl}labores/$id")
        }
    }
}

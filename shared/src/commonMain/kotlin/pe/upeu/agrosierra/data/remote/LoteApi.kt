package pe.upeu.agrosierra.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import pe.upeu.agrosierra.data.remote.dto.LoteResponseDto
import pe.upeu.agrosierra.data.remote.dto.PaginaResponseDto

class LoteApi(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    suspend fun listar(valle: String? = null, pagina: Int = 0, tamanio: Int = 20): Result<PaginaResponseDto<LoteResponseDto>> {
        return ejecutarLlamada {
            httpClient.get("${baseUrl}lotes") {
                if (!valle.isNullOrBlank()) {
                    parameter("valle", valle)
                }
                parameter("pagina", pagina)
                parameter("tamanio", tamanio)
            }
        }
    }

    suspend fun obtener(id: Long): Result<LoteResponseDto> {
        return ejecutarLlamada {
            httpClient.get("${baseUrl}lotes/$id")
        }
    }

    suspend fun cerrarCampania(id: Long): Result<LoteResponseDto> {
        return ejecutarLlamada {
            httpClient.post("${baseUrl}lotes/$id/cerrar-campania")
        }
    }
}

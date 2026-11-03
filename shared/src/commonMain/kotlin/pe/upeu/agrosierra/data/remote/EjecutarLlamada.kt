package pe.upeu.agrosierra.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import pe.upeu.agrosierra.domain.model.ErrorApi

class ErrorApiException(val errorApi: ErrorApi) : Exception(errorApi.toString())

suspend inline fun <reified T> ejecutarLlamada(
    block: () -> HttpResponse
): Result<T> {
    return try {
        val response = block()
        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.Accepted -> {
                Result.success(response.body<T>())
            }
            HttpStatusCode.BadRequest -> {
                val errorText = response.bodyAsText()
                val erroresCampo = parsearErroresValidacion(errorText)
                if (erroresCampo.isNotEmpty()) {
                    Result.failure(ErrorApiException(ErrorApi.Validacion(erroresCampo)))
                } else {
                    Result.failure(ErrorApiException(ErrorApi.Conflicto(errorText.ifBlank { "Solicitud incorrecta" })))
                }
            }
            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> {
                Result.failure(ErrorApiException(ErrorApi.NoAutorizado))
            }
            HttpStatusCode.NotFound -> {
                Result.failure(ErrorApiException(ErrorApi.NoEncontrado))
            }
            HttpStatusCode.Conflict -> {
                val errorText = response.bodyAsText()
                Result.failure(ErrorApiException(ErrorApi.Conflicto(errorText)))
            }
            HttpStatusCode.InternalServerError, HttpStatusCode.BadGateway, HttpStatusCode.ServiceUnavailable -> {
                Result.failure(ErrorApiException(ErrorApi.Servidor))
            }
            else -> {
                Result.failure(ErrorApiException(ErrorApi.Servidor))
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: ErrorApiException) {
        Result.failure(e)
    } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
        Result.failure(ErrorApiException(ErrorApi.TiempoAgotado))
    } catch (e: Exception) {
        Result.failure(ErrorApiException(ErrorApi.SinConexion))
    }
}

fun parsearErroresValidacion(jsonStr: String): Map<String, String> {
    return try {
        val jsonObj = Json.parseToJsonElement(jsonStr).jsonObject
        val map = mutableMapOf<String, String>()
        if (jsonObj.containsKey("errors")) {
            val errorsObj = jsonObj["errors"]?.jsonObject
            errorsObj?.forEach { (k, v) ->
                map[k] = v.jsonPrimitive.content
            }
        }
        map
    } catch (_: Exception) {
        emptyMap()
    }
}

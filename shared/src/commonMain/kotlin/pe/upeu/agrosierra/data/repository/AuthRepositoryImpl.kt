package pe.upeu.agrosierra.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.upeu.agrosierra.data.local.AlmacenSeguro
import pe.upeu.agrosierra.data.remote.dto.LoginRequestDto
import pe.upeu.agrosierra.data.remote.dto.LoginResponseDto
import pe.upeu.agrosierra.data.remote.ejecutarLlamada
import pe.upeu.agrosierra.domain.model.Sesion
import pe.upeu.agrosierra.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val almacenSeguro: AlmacenSeguro,
    private val baseUrl: String
) : AuthRepository {

    override suspend fun login(usuario: String, clave: String): Result<Sesion> {
        val result = ejecutarLlamada<LoginResponseDto> {
            httpClient.post("${baseUrl}auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(usuario = usuario, clave = clave))
            }
        }

        return result.mapCatching { dto ->
            val sesion = Sesion(
                token = dto.token,
                usuario = dto.usuario,
                nombreCompleto = dto.nombreCompleto,
                roles = dto.roles,
                privilegios = dto.privilegios.toSet()
            )
            almacenSeguro.borrarToken()
            almacenSeguro.guardarSesion(sesion)
            almacenSeguro.guardarToken(dto.token)
            sesion
        }
    }

    override suspend fun obtenerSesion(): Sesion? {
        return almacenSeguro.leerSesion()
    }

    override suspend fun cerrarSesion() {
        almacenSeguro.borrarToken()
    }
}

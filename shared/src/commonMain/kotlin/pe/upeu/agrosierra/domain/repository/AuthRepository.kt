package pe.upeu.agrosierra.domain.repository

import pe.upeu.agrosierra.domain.model.Sesion

interface AuthRepository {
    suspend fun login(usuario: String, clave: String): Result<Sesion>
    suspend fun obtenerSesion(): Sesion?
    suspend fun cerrarSesion()
}

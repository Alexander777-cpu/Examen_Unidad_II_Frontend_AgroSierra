package pe.upeu.agrosierra.data.local

import pe.upeu.agrosierra.domain.model.Sesion

interface AlmacenSeguro {
    suspend fun guardarToken(token: String)
    suspend fun leerToken(): String?
    suspend fun borrarToken()
    suspend fun guardarSesion(sesion: Sesion)
    suspend fun leerSesion(): Sesion?
}

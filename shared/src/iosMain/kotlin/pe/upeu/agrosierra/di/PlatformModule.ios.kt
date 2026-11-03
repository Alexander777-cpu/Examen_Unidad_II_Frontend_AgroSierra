package pe.upeu.agrosierra.di

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.upeu.agrosierra.data.local.AlmacenSeguro
import pe.upeu.agrosierra.domain.model.Sesion

class AlmacenSeguroIos : AlmacenSeguro {
    private var tokenStr: String? = null
    private var sesionObj: Sesion? = null

    override suspend fun guardarToken(token: String) { tokenStr = token }
    override suspend fun leerToken(): String? = tokenStr
    override suspend fun borrarToken() { tokenStr = null; sesionObj = null }
    override suspend fun guardarSesion(sesion: Sesion) { sesionObj = sesion; tokenStr = sesion.token }
    override suspend fun leerSesion(): Sesion? = sesionObj
}

actual val platformModule: Module = module {
    single<AlmacenSeguro> { AlmacenSeguroIos() }
    single(named("baseUrl")) { "http://localhost:8080/api/v1/" }
    single<SqlDriver> { error("iOS SqlDriver not configured") }
}

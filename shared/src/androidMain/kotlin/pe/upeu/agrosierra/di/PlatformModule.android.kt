package pe.upeu.agrosierra.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.upeu.agrosierra.data.local.AlmacenSeguro
import pe.upeu.agrosierra.data.local.AlmacenSeguroAndroid
import pe.upeu.agrosierra.db.AgroDb

actual val platformModule: Module = module {
    single<AlmacenSeguro> { AlmacenSeguroAndroid(androidContext()) }
    single(named("baseUrl")) { "http://10.0.2.2:8080/api/v1/" }
    single<SqlDriver> { AndroidSqliteDriver(AgroDb.Schema, androidContext(), "agro_database.db") }
}

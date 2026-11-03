package pe.upeu.agrosierra.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.upeu.agrosierra.data.local.AlmacenSeguro
import pe.upeu.agrosierra.data.local.AlmacenSeguroAndroid

actual val platformModule: Module = module {
    single<AlmacenSeguro> { AlmacenSeguroAndroid(androidContext()) }
    single(named("baseUrl")) { "http://10.0.2.2:8080/api/v1/" }
}

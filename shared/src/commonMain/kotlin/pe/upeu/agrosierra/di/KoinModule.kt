package pe.upeu.agrosierra.di

import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.agrosierra.data.remote.HttpClientFactory
import pe.upeu.agrosierra.data.remote.LaborApi
import pe.upeu.agrosierra.data.remote.LoteApi
import pe.upeu.agrosierra.data.repository.AuthRepositoryImpl
import pe.upeu.agrosierra.data.repository.LaborRepositoryRest
import pe.upeu.agrosierra.data.repository.LoteRepositoryRest
import pe.upeu.agrosierra.data.repository.SyncManager
import pe.upeu.agrosierra.db.AgroDb
import pe.upeu.agrosierra.domain.repository.AuthRepository
import pe.upeu.agrosierra.domain.repository.LaborRepository
import pe.upeu.agrosierra.domain.repository.LoteRepository
import pe.upeu.agrosierra.presentation.LoginViewModel
import pe.upeu.agrosierra.presentation.detalle.DetalleLoteViewModel
import pe.upeu.agrosierra.presentation.labor.FormularioLaborViewModel
import pe.upeu.agrosierra.presentation.padron.LotesViewModel

val sharedModule = module {
    single { AgroDb(get()) }
    single { HttpClientFactory(get()).create() }
    single { LoteApi(get(), get(named("baseUrl"))) }
    single { LaborApi(get(), get(named("baseUrl"))) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(named("baseUrl"))) }
    single<LoteRepository> { LoteRepositoryRest(get(), get()) }
    single<LaborRepository> { LaborRepositoryRest(get(), get()) }
    single { SyncManager(get(), get(), get()) }

    factory { LoginViewModel(get()) }
    factory { LotesViewModel(get(), get(), get()) }
    factory { (loteId: Long) -> DetalleLoteViewModel(loteId, get(), get(), get(), get()) }
    factory { (loteId: Long, laborId: Long?) -> FormularioLaborViewModel(loteId, laborId, get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule, sharedModule)
    }

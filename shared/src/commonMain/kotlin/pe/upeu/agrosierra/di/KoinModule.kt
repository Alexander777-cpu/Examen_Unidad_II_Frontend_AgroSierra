package pe.upeu.agrosierra.di

import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.agrosierra.data.remote.HttpClientFactory
import pe.upeu.agrosierra.data.repository.AuthRepositoryImpl
import pe.upeu.agrosierra.domain.repository.AuthRepository
import pe.upeu.agrosierra.presentation.LoginViewModel

val sharedModule = module {
    single { HttpClientFactory(get()).create() }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(named("baseUrl"))) }
    factory { LoginViewModel(get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule, sharedModule)
    }

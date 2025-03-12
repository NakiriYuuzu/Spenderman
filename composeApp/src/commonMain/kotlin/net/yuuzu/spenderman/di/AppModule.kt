package net.yuuzu.spenderman.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    println("initKoin[DEBUG]: startKoin")
    appDeclaration()
    modules(
        dataSourceModule,
        repositoryModule,
        viewModelModule,
        sampleDataModule
    )
}

// Called by iOS
fun initKoin() = initKoin {}

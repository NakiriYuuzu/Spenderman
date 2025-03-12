package net.yuuzu.spenderman.di

import net.yuuzu.spenderman.data.repository.*
import net.yuuzu.spenderman.data.repository.impl.*
import org.koin.dsl.module

val repositoryModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get()) }
    single<BudgetRepository> { BudgetRepositoryImpl(get(), get()) }
    single<PaymentMethodRepository> { PaymentMethodRepositoryImpl(get()) }
    single<TagRepository> { TagRepositoryImpl(get(), get()) }
}

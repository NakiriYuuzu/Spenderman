package net.yuuzu.spenderman.di

import net.yuuzu.spenderman.ui.viewmodel.CategoryViewModel
import net.yuuzu.spenderman.ui.viewmodel.ExpenseViewModel
import net.yuuzu.spenderman.ui.viewmodel.HomeViewModel
import net.yuuzu.spenderman.ui.viewmodel.SettingsViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { HomeViewModel(get(), get(), get(), get()) }
    factory { ExpenseViewModel(get(), get(), get(), get()) }
    factory { SettingsViewModel(get()) }
    factory { CategoryViewModel(get()) }
}

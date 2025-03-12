package net.yuuzu.spenderman.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import net.yuuzu.spenderman.data.source.*
import org.koin.dsl.module

val dataSourceModule = module {
    single { Settings() }
    
    single { SettingsDataSource(get()) }
    single { ExpenseDataSource(get()) }
    single { CategoryDataSource(get()) }
    single { BudgetDataSource(get()) }
    single { PaymentMethodDataSource(get()) }
    single { TagDataSource(get()) }
}

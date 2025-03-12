package net.yuuzu.spenderman.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.yuuzu.spenderman.data.repository.BudgetRepository
import net.yuuzu.spenderman.data.repository.CategoryRepository
import net.yuuzu.spenderman.data.repository.ExpenseRepository
import net.yuuzu.spenderman.data.repository.PaymentMethodRepository
import net.yuuzu.spenderman.data.repository.TagRepository
import net.yuuzu.spenderman.util.SampleData
import org.koin.dsl.module

val sampleDataModule = module {
    single {
        // Initialize sample data
        val categoryRepository = get<CategoryRepository>()
        val expenseRepository = get<ExpenseRepository>()
        val budgetRepository = get<BudgetRepository>()
        val paymentMethodRepository = get<PaymentMethodRepository>()
        val tagRepository = get<TagRepository>()
        
        CoroutineScope(Dispatchers.Default).launch {
            SampleData.populateRepositories(
                categoryRepository,
                expenseRepository,
                budgetRepository,
                paymentMethodRepository,
                tagRepository
            )
        }
    }
}

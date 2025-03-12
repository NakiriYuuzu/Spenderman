package net.yuuzu.spenderman.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.yuuzu.spenderman.util.getDaysInMonth
import net.yuuzu.spenderman.data.model.Budget
import net.yuuzu.spenderman.data.model.Category
import net.yuuzu.spenderman.data.model.Expense
import net.yuuzu.spenderman.data.model.PaymentMethod
import net.yuuzu.spenderman.data.model.RecurringType
import net.yuuzu.spenderman.data.model.Tag
import kotlin.random.Random

object SampleData {
    
    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private val currentMonth = now.month
    private val currentYear = now.year
    
    // Sample Categories
    val categories = listOf(
        Category(
            id = "cat1",
            name = "Food",
            color = "#4CAF50",
            icon = "food",
            isIncome = false,
            budget = 500.0
        ),
        Category(
            id = "cat2",
            name = "Shopping",
            color = "#2196F3",
            icon = "shopping",
            isIncome = false,
            budget = 300.0
        ),
        Category(
            id = "cat3",
            name = "Transport",
            color = "#FF9800",
            icon = "transport",
            isIncome = false,
            budget = 200.0
        ),
        Category(
            id = "cat4",
            name = "Home",
            color = "#9C27B0",
            icon = "home",
            isIncome = false,
            budget = 1000.0
        ),
        Category(
            id = "cat5",
            name = "Salary",
            color = "#4CAF50",
            icon = "shopping",
            isIncome = true
        ),
        Category(
            id = "cat6",
            name = "Gifts",
            color = "#E91E63",
            icon = "shopping",
            isIncome = true
        )
    )
    
    // Sample Payment Methods
    val paymentMethods = listOf(
        PaymentMethod(
            id = "pm1",
            name = "Cash",
            icon = "cash",
            color = "#4CAF50",
            isDefault = true
        ),
        PaymentMethod(
            id = "pm2",
            name = "Credit Card",
            icon = "credit_card",
            color = "#2196F3"
        ),
        PaymentMethod(
            id = "pm3",
            name = "Bank Transfer",
            icon = "bank",
            color = "#FF9800"
        )
    )
    
    // Sample Tags
    val tags = listOf(
        Tag(
            id = "tag1",
            name = "Personal",
            color = "#4CAF50"
        ),
        Tag(
            id = "tag2",
            name = "Work",
            color = "#2196F3"
        ),
        Tag(
            id = "tag3",
            name = "Family",
            color = "#FF9800"
        ),
        Tag(
            id = "tag4",
            name = "Vacation",
            color = "#9C27B0"
        )
    )
    
    // Sample Expenses
    val expenses = listOf(
        Expense(
            id = "exp1",
            amount = 25.50,
            category = "cat1",
            description = "Lunch at restaurant",
            date = LocalDateTime(currentYear, currentMonth, now.dayOfMonth - 1, 12, 30),
            paymentMethod = "pm1"
        ),
        Expense(
            id = "exp2",
            amount = 150.0,
            category = "cat2",
            description = "New clothes",
            date = LocalDateTime(currentYear, currentMonth, now.dayOfMonth - 2, 15, 45),
            paymentMethod = "pm2",
            tags = listOf("tag1")
        ),
        Expense(
            id = "exp3",
            amount = 35.0,
            category = "cat3",
            description = "Taxi ride",
            date = LocalDateTime(currentYear, currentMonth, now.dayOfMonth - 3, 18, 20),
            paymentMethod = "pm1"
        ),
        Expense(
            id = "exp4",
            amount = 2500.0,
            category = "cat5",
            description = "Monthly salary",
            date = LocalDateTime(currentYear, currentMonth, 1, 9, 0),
            isIncome = true,
            paymentMethod = "pm3"
        ),
        Expense(
            id = "exp5",
            amount = 500.0,
            category = "cat4",
            description = "Rent",
            date = LocalDateTime(currentYear, currentMonth, 5, 10, 0),
            paymentMethod = "pm3",
            recurringType = RecurringType.MONTHLY
        ),
        Expense(
            id = "exp6",
            amount = 100.0,
            category = "cat6",
            description = "Birthday gift",
            date = LocalDateTime(currentYear, currentMonth, now.dayOfMonth - 5, 14, 30),
            isIncome = true,
            paymentMethod = "pm1",
            tags = listOf("tag3")
        )
    )
    
    // Sample Budgets
    val budgets = listOf(
        Budget(
            id = "budget1",
            amount = 500.0,
            startDate = LocalDate(currentYear, currentMonth, 1),
            endDate = LocalDate(currentYear, currentMonth, getDaysInMonth(currentMonth, currentYear)),
            categoryId = "cat1",
            name = "Food Budget"
        ),
        Budget(
            id = "budget2",
            amount = 300.0,
            startDate = LocalDate(currentYear, currentMonth, 1),
            endDate = LocalDate(currentYear, currentMonth, getDaysInMonth(currentMonth, currentYear)),
            categoryId = "cat2",
            name = "Shopping Budget"
        ),
        Budget(
            id = "budget3",
            amount = 2000.0,
            startDate = LocalDate(currentYear, currentMonth, 1),
            endDate = LocalDate(currentYear, currentMonth, getDaysInMonth(currentMonth, currentYear)),
            name = "Monthly Budget"
        )
    )
    
    // Function to populate repositories with sample data
    suspend fun populateRepositories(
        categoryRepository: net.yuuzu.spenderman.data.repository.CategoryRepository,
        expenseRepository: net.yuuzu.spenderman.data.repository.ExpenseRepository,
        budgetRepository: net.yuuzu.spenderman.data.repository.BudgetRepository,
        paymentMethodRepository: net.yuuzu.spenderman.data.repository.PaymentMethodRepository,
        tagRepository: net.yuuzu.spenderman.data.repository.TagRepository
    ) {
        // Add categories
        categories.forEach { categoryRepository.add(it) }
        
        // Add payment methods
        paymentMethods.forEach { paymentMethodRepository.add(it) }
        
        // Add tags
        tags.forEach { tagRepository.add(it) }
        
        // Add expenses
        expenses.forEach { expenseRepository.add(it) }
        
        // Add budgets
        budgets.forEach { budgetRepository.add(it) }
    }
}

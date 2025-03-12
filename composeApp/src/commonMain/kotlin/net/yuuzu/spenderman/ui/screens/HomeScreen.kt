package net.yuuzu.spenderman.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.yuuzu.spenderman.ui.components.BudgetProgressCard
import net.yuuzu.spenderman.ui.components.ExpenseItem
import net.yuuzu.spenderman.ui.components.SummaryCardCompact
import net.yuuzu.spenderman.ui.components.SummaryType
import net.yuuzu.spenderman.util.LocalScreenSize
import net.yuuzu.spenderman.ui.viewmodel.HomeViewModel

enum class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    CATEGORIES("Categories", Icons.Filled.Category, Icons.Outlined.Category),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddExpenseClick: () -> Unit,
    onExpenseClick: (String) -> Unit,
    onBudgetClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onCategoriesClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Navigation state
    var selectedItem by remember { mutableStateOf(NavigationItem.HOME) }

    // 手機畫面：底部導航列
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spenderman") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedItem == NavigationItem.HOME,
                    onClick = { selectedItem = NavigationItem.HOME },
                    icon = {
                        Icon(
                            imageVector = if (selectedItem == NavigationItem.HOME)
                                NavigationItem.HOME.selectedIcon else NavigationItem.HOME.unselectedIcon,
                            contentDescription = NavigationItem.HOME.title
                        )
                    },
                    label = { Text(NavigationItem.HOME.title) }
                )

                NavigationBarItem(
                    selected = selectedItem == NavigationItem.CATEGORIES,
                    onClick = {
                        selectedItem = NavigationItem.CATEGORIES
                        onCategoriesClick()
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedItem == NavigationItem.CATEGORIES)
                                NavigationItem.CATEGORIES.selectedIcon else NavigationItem.CATEGORIES.unselectedIcon,
                            contentDescription = NavigationItem.CATEGORIES.title
                        )
                    },
                    label = { Text(NavigationItem.CATEGORIES.title) }
                )

                NavigationBarItem(
                    selected = selectedItem == NavigationItem.SETTINGS,
                    onClick = {
                        selectedItem = NavigationItem.SETTINGS
                        onSettingsClick()
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedItem == NavigationItem.SETTINGS)
                                NavigationItem.SETTINGS.selectedIcon else NavigationItem.SETTINGS.unselectedIcon,
                            contentDescription = NavigationItem.SETTINGS.title
                        )
                    },
                    label = { Text(NavigationItem.SETTINGS.title) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // 手機畫面：單欄佈局
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 摘要卡片
                item {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 修改這裡：將 Income 和 Expense 放在同一列
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Income 和 Expense 放在同一列
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryCardCompact(
                                title = "Income",
                                amount = state.totalIncome,
                                type = SummaryType.INCOME,
                                modifier = Modifier.weight(1f)
                            )

                            SummaryCardCompact(
                                title = "Expense",
                                amount = state.totalExpense,
                                type = SummaryType.EXPENSE,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Balance 單獨一列
                        SummaryCardCompact(
                            title = "Balance",
                            amount = state.totalIncome - state.totalExpense,
                            type = SummaryType.BALANCE,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 活躍預算
                if (state.activeBudgets.isNotEmpty()) {
                    item {
                        Text(
                            text = "Active Budgets",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(state.activeBudgets) { budget ->
                        val category = state.categories.find { it.id == budget.categoryId }
                        BudgetProgressCard(
                            budget = budget,
                            category = category,
                            progress = state.budgetProgress[budget.id] ?: 0f,
                            onClick = { onBudgetClick(budget.id) }
                        )
                    }
                }

                // 最近交易
                if (state.recentTransactions.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(state.recentTransactions) { expense ->
                        val category = state.categories.find { it.id == expense.category }
                        ExpenseItem(
                            expense = expense,
                            category = category,
                            onClick = { onExpenseClick(expense.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // FAB的空間
                    }
                }
            }
        }
    }
}
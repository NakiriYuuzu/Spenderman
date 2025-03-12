package net.yuuzu.spenderman.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.yuuzu.spenderman.data.model.Category
import net.yuuzu.spenderman.util.LocalScreenSize
import net.yuuzu.spenderman.ui.viewmodel.CategoryEvent
import net.yuuzu.spenderman.ui.viewmodel.CategoryViewModel
import net.yuuzu.spenderman.util.extensions.formatAsCurrency
import net.yuuzu.spenderman.util.parseColor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val screenSize = LocalScreenSize.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    
    // Handle errors
    LaunchedEffect(state.error) {
        if (state.error != null) {
            snackbarHostState.showSnackbar(state.error!!)
        }
    }
    
    // Determine grid columns based on screen width
    val gridColumns = when {
        screenSize.width > 840.dp -> GridCells.Fixed(4)  // Expanded
        screenSize.width > 600.dp -> GridCells.Fixed(3)  // Medium
        else -> GridCells.Fixed(2)  // Compact
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Category"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Tabs for Income/Expense categories with improved styling
            TabRow(
                selectedTabIndex = if (state.showIncomeCategories) 1 else 0,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            tabPositions[if (state.showIncomeCategories) 1 else 0]
                        ),
                        height = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = !state.showIncomeCategories,
                    onClick = { viewModel.onEvent(CategoryEvent.ToggleIncomeFilter(false)) },
                    text = { 
                        Text(
                            "Expense Categories",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (!state.showIncomeCategories) FontWeight.Bold else FontWeight.Normal
                            )
                        ) 
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (!state.showIncomeCategories) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                )
                Tab(
                    selected = state.showIncomeCategories,
                    onClick = { viewModel.onEvent(CategoryEvent.ToggleIncomeFilter(true)) },
                    text = { 
                        Text(
                            "Income Categories",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (state.showIncomeCategories) FontWeight.Bold else FontWeight.Normal
                            )
                        ) 
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (state.showIncomeCategories) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredCategories = if (state.showIncomeCategories) {
                    state.categories.filter { it.isIncome }
                } else {
                    state.categories.filter { !it.isIncome }
                }
                
                if (filteredCategories.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (state.showIncomeCategories) 
                                    Icons.Default.ArrowUpward 
                                else 
                                    Icons.Default.Category,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "No ${if (state.showIncomeCategories) "income" else "expense"} categories yet",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Tap + to add one",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = gridColumns,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredCategories) { category ->
                            CategoryCard(
                                category = category,
                                onClick = {
                                    selectedCategory = category
                                    showEditCategoryDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Add Category Dialog
        if (showAddCategoryDialog) {
            CategoryDialog(
                category = null,
                isIncome = state.showIncomeCategories,
                onDismiss = { showAddCategoryDialog = false },
                onSave = { newCategory ->
                    viewModel.onEvent(CategoryEvent.AddCategory(newCategory))
                    showAddCategoryDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Category added successfully")
                    }
                }
            )
        }
        
        // Edit Category Dialog
        if (showEditCategoryDialog && selectedCategory != null) {
            CategoryDialog(
                category = selectedCategory,
                isIncome = selectedCategory?.isIncome ?: state.showIncomeCategories,
                onDismiss = { showEditCategoryDialog = false },
                onSave = { updatedCategory ->
                    viewModel.onEvent(CategoryEvent.UpdateCategory(updatedCategory))
                    showEditCategoryDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Category updated successfully")
                    }
                },
                onDelete = {
                    showEditCategoryDialog = false
                    showDeleteConfirmDialog = true
                }
            )
        }
        
        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog && selectedCategory != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Delete Category") },
                text = { Text("Are you sure you want to delete this category? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedCategory?.let { category ->
                                viewModel.onEvent(CategoryEvent.DeleteCategory(category.id))
                                scope.launch {
                                    snackbarHostState.showSnackbar("Category deleted successfully")
                                }
                            }
                            showDeleteConfirmDialog = false
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun CategoryDialog(
    category: Category?,
    isIncome: Boolean,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val isNewCategory = category == null
    val title = if (isNewCategory) "Add Category" else "Edit Category"
    
    var name by remember { mutableStateOf(category?.name ?: "") }
    var selectedColor by remember { mutableStateOf(category?.color ?: "#4CAF50") }
    var selectedIcon by remember { mutableStateOf(category?.icon ?: "shopping") }
    var budget by remember { mutableStateOf(category?.budget?.toString() ?: "0.0") }
    var showColorPicker by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            ) 
        },
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                // Type (Income/Expense)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Category Type:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = if (isIncome) "Income" else "Expense",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                
                // Budget field (only for expense categories)
                if (!isIncome) {
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { 
                            // Only allow numbers and decimal point
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                budget = it
                            }
                        },
                        label = { Text("Budget (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        prefix = { Text("$") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Color picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showColorPicker = !showColorPicker }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Color:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                try {
                                    parseColor(selectedColor)
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                }
                
                // Color picker expanded
                AnimatedVisibility(
                    visible = showColorPicker,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        val colors = listOf(
                            "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
                            "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
                            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
                            "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#000000"
                        )
                        
                        items(colors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(parseColor(color))
                                    .border(
                                        width = if (selectedColor == color) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = color }
                            )
                        }
                    }
                }
                
                // Icon picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showIconPicker = !showIconPicker }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Icon:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconByName(selectedIcon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Icon picker expanded
                AnimatedVisibility(
                    visible = showIconPicker,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        val icons = listOf(
                            "food" to Icons.Default.Fastfood,
                            "shopping" to Icons.Default.ShoppingBag,
                            "grocery" to Icons.Default.LocalGroceryStore,
                            "transport" to Icons.Default.Train,
                            "home" to Icons.Default.Home,
                            "tech" to Icons.Default.Smartphone,
                            "health" to Icons.Default.HealthAndSafety,
                            "education" to Icons.Default.School,
                            "entertainment" to Icons.Default.Movie,
                            "travel" to Icons.Default.Flight,
                            "gift" to Icons.Default.CardGiftcard,
                            "bill" to Icons.Default.Receipt,
                            "salary" to Icons.Default.AttachMoney,
                            "investment" to Icons.AutoMirrored.Filled.TrendingUp,
                            "other" to Icons.Default.MoreHoriz
                        )
                        
                        items(icons) { (name, icon) ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selectedIcon == name)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedIcon = name },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = name,
                                    tint = if (selectedIcon == name)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
                
                // Delete button (only for editing)
                if (!isNewCategory && onDelete != null) {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Category")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newCategory = Category(
                        id = category?.id ?: Uuid.random().toString(),
                        name = name,
                        color = selectedColor,
                        icon = selectedIcon,
                        isIncome = isIncome,
                        budget = budget.toDoubleOrNull() ?: 0.0
                    )
                    onSave(newCategory)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun getCategoryIcon(category: Category): ImageVector {
    return getIconByName(category.icon)
}

@Composable
private fun getIconByName(iconName: String): ImageVector {
    return when (iconName) {
        "food" -> Icons.Default.Fastfood
        "shopping" -> Icons.Default.ShoppingBag
        "grocery" -> Icons.Default.LocalGroceryStore
        "transport" -> Icons.Default.Train
        "home" -> Icons.Default.Home
        "tech" -> Icons.Default.Smartphone
        "health" -> Icons.Default.HealthAndSafety
        "education" -> Icons.Default.School
        "entertainment" -> Icons.Default.Movie
        "travel" -> Icons.Default.Flight
        "gift" -> Icons.Default.CardGiftcard
        "bill" -> Icons.Default.Receipt
        "salary" -> Icons.Default.AttachMoney
        "investment" -> Icons.AutoMirrored.Filled.TrendingUp
        else -> Icons.Default.MoreHoriz
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    val backgroundColor = try {
        parseColor(category.color)
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
    
    // Format budget with commas and 2 decimal places if present
    val formattedBudget = if (category.budget > 0) {
        category.budget.formatAsCurrency()
    } else {
        null
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category),
                    contentDescription = category.name,
                    tint = backgroundColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            
            if (formattedBudget != null) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$$formattedBudget",
                        style = MaterialTheme.typography.bodyMedium,
                        color = backgroundColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

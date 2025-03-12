package net.yuuzu.spenderman.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import net.yuuzu.spenderman.data.model.RecurringType
import net.yuuzu.spenderman.ui.components.CategorySelector
import net.yuuzu.spenderman.ui.viewmodel.ExpenseEvent
import net.yuuzu.spenderman.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    expenseId: String? = null,
    onBackClick: () -> Unit,
    onSaveComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    // Load expense if editing
    LaunchedEffect(expenseId) {
        if (!expenseId.isNullOrBlank()) {
            viewModel.onEvent(ExpenseEvent.LoadExpense(expenseId))
        }
    }
    
    // Handle save completion
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onSaveComplete()
        }
    }
    
    // Handle errors
    LaunchedEffect(state.error) {
        if (state.error != null) {
            snackbarHostState.showSnackbar(state.error!!)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expenseId.isNullOrBlank()) "Add Expense" else "Edit Expense") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ExpenseEvent.SaveExpense) }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Income/Expense Toggle
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = !state.isIncome,
                    onClick = { viewModel.onEvent(ExpenseEvent.IsIncomeChanged(false)) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Expense")
                }
                SegmentedButton(
                    selected = state.isIncome,
                    onClick = { viewModel.onEvent(ExpenseEvent.IsIncomeChanged(true)) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Income")
                }
            }
            
            // Amount
            OutlinedTextField(
                value = if (state.amount == 0.0) "" else state.amount.toString(),
                onValueChange = { 
                    val amount = it.toDoubleOrNull() ?: 0.0
                    viewModel.onEvent(ExpenseEvent.AmountChanged(amount))
                },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(ExpenseEvent.DescriptionChanged(it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Date and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = formatDate(state.date),
                    onValueChange = { },
                    label = { Text("Date") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Select Date"
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = formatTime(state.date),
                    onValueChange = { },
                    label = { Text("Time") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Select Time"
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Category Selector
            CategorySelector(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                onCategorySelected = { viewModel.onEvent(ExpenseEvent.CategorySelected(it)) },
                isIncome = state.isIncome
            )
            
            // Recurring Type
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                RecurringType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.recurringType == type,
                        onClick = { viewModel.onEvent(ExpenseEvent.RecurringTypeSelected(type)) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = RecurringType.entries.size
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(type.name.lowercase().capitalize())
                    }
                }
            }
            
            // Save Button
            Button(
                onClick = { viewModel.onEvent(ExpenseEvent.SaveExpense) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
            
            // Delete Button (only for editing)
            if (!expenseId.isNullOrBlank()) {
                Button(
                    onClick = { /* Delete functionality */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("Delete")
                }
            }
        }
        
        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = state.date.toEpochMillis()
            )
            
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val instant = Instant.fromEpochMilliseconds(millis)
                                val newDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                                val updatedDate = LocalDateTime(
                                    newDate.date.year,
                                    newDate.date.month,
                                    newDate.date.dayOfMonth,
                                    state.date.hour,
                                    state.date.minute,
                                    state.date.second,
                                    state.date.nanosecond
                                )
                                viewModel.onEvent(ExpenseEvent.DateChanged(updatedDate))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        // Time Picker Dialog
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = state.date.hour,
                initialMinute = state.date.minute
            )
            
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val updatedDate = LocalDateTime(
                                state.date.date.year,
                                state.date.date.month,
                                state.date.date.dayOfMonth,
                                timePickerState.hour,
                                timePickerState.minute,
                                state.date.second,
                                state.date.nanosecond
                            )
                            viewModel.onEvent(ExpenseEvent.DateChanged(updatedDate))
                            showTimePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TimePicker(state = timePickerState)
                    }
                }
            )
        }
    }
}

private fun formatDate(date: LocalDateTime): String {
    return "${date.date.dayOfMonth}/${date.date.monthNumber}/${date.date.year}"
}

private fun formatTime(date: LocalDateTime): String {
    return "${date.hour}:${date.minute.toString().padStart(2, '0')}"
}

private fun LocalDateTime.toEpochMillis(): Long {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return instant.toEpochMilliseconds()
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

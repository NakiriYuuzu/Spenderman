package net.yuuzu.spenderman.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import net.yuuzu.spenderman.data.model.Category
import net.yuuzu.spenderman.data.model.Expense
import net.yuuzu.spenderman.util.extensions.formatAsCurrency
import net.yuuzu.spenderman.util.parseColor
import kotlin.math.abs

@Composable
fun ExpenseItem(
    expense: Expense,
    category: Category?,
    currencySymbol: String = "$",
    onClick: () -> Unit
) {
    // Format amount with commas and 2 decimal places
    val formattedAmount = abs(expense.amount).formatAsCurrency()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            val categoryColor = getCategoryColor(category)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category),
                    contentDescription = category?.name ?: "Category",
                    tint = categoryColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Description and date
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.description.ifEmpty { category?.name ?: "Expense" },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatDate(expense.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (expense.paymentMethod.isNotEmpty()) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = expense.paymentMethod,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Amount
            val amountColor = if (expense.isIncome) 
                MaterialTheme.colorScheme.primary
            else 
                MaterialTheme.colorScheme.error
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(amountColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (expense.isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = if (expense.isIncome) "Income" else "Expense",
                        tint = amountColor,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Text(
                        text = "$currencySymbol$formattedAmount",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = amountColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun getCategoryColor(category: Category?): Color {
    return category?.let {
        try {
            parseColor(it.color)
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }
    } ?: MaterialTheme.colorScheme.primary
}

@Composable
private fun getCategoryIcon(category: Category?): ImageVector {
    return when (category?.icon) {
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

private fun formatDate(date: LocalDateTime): String {
    val today = kotlinx.datetime.Clock.System.now()
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    return when {
        date.date == today.date -> "Today, ${date.hour}:${date.minute.toString().padStart(2, '0')}"
        date.date.year == today.date.year -> "${date.date.dayOfMonth} ${date.date.month.name.take(3)}, ${date.hour}:${date.minute.toString().padStart(2, '0')}"
        else -> "${date.date.dayOfMonth} ${date.date.month.name.take(3)} ${date.date.year}, ${date.hour}:${date.minute.toString().padStart(2, '0')}"
    }
}

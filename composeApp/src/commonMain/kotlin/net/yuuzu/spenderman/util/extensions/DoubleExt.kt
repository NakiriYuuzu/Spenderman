package net.yuuzu.spenderman.util.extensions


fun Double.formatAsCurrency(): String {
    val rounded = kotlin.math.round(this * 100) / 100

    // 處理整數部分和小數部分
    val intPart = rounded.toLong()
    val decimalPart = ((rounded - intPart) * 100).toInt().toString().padStart(2, '0')

    // 處理千分位分隔符
    val intPartWithSeparator = intPart.toString().reversed().chunked(3).joinToString(",").reversed()

    return "$intPartWithSeparator.$decimalPart"
}

fun Double.formatAsCurrency(currencySymbol: String): String {
    return "$currencySymbol${this.formatAsCurrency()}"
}
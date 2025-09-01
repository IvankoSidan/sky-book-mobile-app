package com.wheezy.myjetpackproject.Utils

object CardFormatUtils {

    fun formatMaskedCardNumber(input: String): String {
        val digits = input.filter { it.isDigit() }.padEnd(16, '•')
        return digits.chunked(4).mapIndexed { i, chunk ->
            if (i < 3) "****" else chunk
        }.joinToString(" ")
    }

    fun formatExpiryDateInput(input: String): String {
        val digits = input.filter { it.isDigit() }.take(4)
        return when {
            digits.length <= 2 -> digits
            else -> digits.substring(0, 2) + "/" + digits.substring(2)
        }
    }
}
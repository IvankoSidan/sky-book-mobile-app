package com.wheezy.myjetpackproject.core.common.utils

object PaymentCardUtils {

    fun validateCardNumber(input: String): Boolean {
        val digits = input.filter { it.isDigit() }
        if (digits.length !in 13..19) return false
        return luhnCheck(digits)
    }

    private fun luhnCheck(number: String): Boolean {
        val sum = number.reversed()
            .mapIndexed { idx, ch ->
                var digit = ch - '0'
                if (idx % 2 == 1) {
                    digit *= 2
                    if (digit > 9) digit -= 9
                }
                digit
            }
            .sum()
        return sum % 10 == 0
    }

    fun validateCardHolder(input: String): Boolean {
        return input.trim().length >= 2
                && input.all { it.isLetter() || it.isWhitespace() }
    }

    fun validateExpiryDate(input: String): Boolean {
        val parts = input.split("/")
        if (parts.size != 2) return false
        val (mm, yy) = parts
        if (mm.length != 2 || yy.length != 2) return false
        val month = mm.toIntOrNull() ?: return false
        val year = ("20$yy").toIntOrNull() ?: return false
        if (month !in 1..12) return false

        val now = java.time.YearMonth.now()
        val expiry = java.time.YearMonth.of(year, month)
        return !expiry.isBefore(now)
    }

    fun validateCvv(input: String): Boolean {
        return input.length in 3..4 && input.all { it.isDigit() }
    }
}

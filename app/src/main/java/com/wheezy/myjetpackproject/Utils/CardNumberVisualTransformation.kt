package com.wheezy.myjetpackproject.Utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Берём до 16 цифр
        val digits = text.text.filter { it.isDigit() }.take(16)

        // Если нет цифр — сразу возвращаем пустую строку с identity-mapping
        if (digits.isEmpty()) {
            val identityMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int = 0
                override fun transformedToOriginal(offset: Int): Int = 0
            }
            return TransformedText(AnnotatedString(""), identityMapping)
        }

        // Разбиваем на группы по 4
        val groups = digits.chunked(4)
        // Маскируем все полностью заполненные группы, кроме последней
        val maskedGroups = groups.mapIndexed { idx, grp ->
            if (idx < groups.lastIndex && grp.length == 4) {
                "****"
            } else {
                grp.padEnd(4, '•')
            }
        }
        // Склеиваем с пробелами
        val formatted = maskedGroups.joinToString(" ")

        // OffsetMapping для корректного перемещения курсора
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // не больше количества цифр
                val capped = offset.coerceIn(0, digits.length)
                // сколько пробелов вставлено до этой позиции
                val spacesBefore = capped / 4
                return (capped + spacesBefore).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // не больше длины отформатированной строки
                val capped = offset.coerceIn(0, formatted.length)
                // сколько полных блоков по 5 символов (4 цифры + пробел) прошло
                val blocks = capped / 5
                val orig = capped - blocks
                return orig.coerceIn(0, digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
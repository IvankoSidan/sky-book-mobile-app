package com.wheezy.myjetpackproject.core.common.utils

object AuthValidator {
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun isNameValid(name: String): Boolean {
        return name.trim().isNotEmpty()
    }
}

package com.wheezy.myjetpackproject.Utils

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

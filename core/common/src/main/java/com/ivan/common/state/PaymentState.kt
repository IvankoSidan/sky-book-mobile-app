package com.wheezy.myjetpackproject.core.common.state

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val paymentId: Long) : PaymentState()
    data class Error(val message: String) : PaymentState()
}
package com.wheezy.myjetpackproject.data.repository

import com.wheezy.myjetpackproject.core.network.ApiService
import com.wheezy.myjetpackproject.core.network.model.PaymentSheetRequest
import com.wheezy.myjetpackproject.core.network.model.PaymentSheetResponseDTO
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import okhttp3.ResponseBody.Companion.toResponseBody

@ActivityRetainedScoped
class PaymentRepository @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) {
    suspend fun createPaymentSheet(
        bookingId: Long,
        amount: Long,
        currency: String = "USD"
    ): Response<PaymentSheetResponseDTO> {
        val token = authRepository.getToken()
            ?: return Response.error(401, "Unauthorized".toResponseBody(null))
        val request = PaymentSheetRequest(bookingId, amount, currency)
        return apiService.createPaymentSheet("Bearer $token", request)
    }
}


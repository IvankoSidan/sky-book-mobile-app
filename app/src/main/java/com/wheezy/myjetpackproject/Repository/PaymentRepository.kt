package com.wheezy.myjetpackproject.Repository

import com.wheezy.myjetpackproject.Data.Dto.PaymentSheetRequest
import com.wheezy.myjetpackproject.Data.Dto.PaymentSheetResponseDTO
import com.wheezy.myjetpackproject.Network.ApiService
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

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
            ?: return Response.error(401, ResponseBody.create(null, "Unauthorized"))

        val request = PaymentSheetRequest(bookingId, amount, currency)
        return apiService.createPaymentSheet("Bearer $token", request)
    }
}


package com.wheezy.myjetpackproject.Repository

import android.util.Log
import com.wheezy.myjetpackproject.Data.Dto.AuthResponse
import com.wheezy.myjetpackproject.Data.Dto.GoogleAuthDto
import com.wheezy.myjetpackproject.Data.Dto.UserLoginDto
import com.wheezy.myjetpackproject.Data.Dto.UserRegisterDto
import com.wheezy.myjetpackproject.Data.Dto.UserResponseDto
import com.wheezy.myjetpackproject.Network.ApiService
import com.wheezy.myjetpackproject.Utils.AuthPreferences
import kotlinx.coroutines.flow.first
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import okhttp3.ResponseBody.Companion.toResponseBody


class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    private var cachedUser: UserResponseDto? = null
    private var lastFetchTime: Long = 0
    private val cacheTimeout = TimeUnit.MINUTES.toMillis(5)

    suspend fun login(dto: UserLoginDto): Response<AuthResponse> {
        return try {
            val response = apiService.login(dto)
            Log.d("AuthRepository", "login() response code: ${response.code()} body: ${response.body()} error: ${response.errorBody()?.string()}")
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    updateCache(authResponse.user, authResponse.token)
                }
            }
            response
        } catch (e: Exception) {
            Log.e("AuthRepository", "login() exception: ${e.message}", e)
            Response.error(400, "Error: ${e.message}".toResponseBody(null))
        }
    }

    suspend fun register(dto: UserRegisterDto): Response<AuthResponse> {
        return try {
            val response = apiService.register(dto)
            Log.d("AuthRepository", "register() response code: ${response.code()} body: ${response.body()} error: ${response.errorBody()?.string()}")
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    updateCache(authResponse.user, authResponse.token)
                }
            }
            response
        } catch (e: Exception) {
            Log.e("AuthRepository", "register() exception: ${e.message}", e)
            Response.error(400, "Error: ${e.message}".toResponseBody(null))
        }
    }

    suspend fun googleAuth(token: String): Response<AuthResponse> {
        return try {
            val response = apiService.googleAuth(GoogleAuthDto(token))
            Log.d("AuthRepository", "googleAuth() response code: ${response.code()} body: ${response.body()} error: ${response.errorBody()?.string()}")
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    updateCache(authResponse.user, authResponse.token)
                }
            }
            response
        } catch (e: Exception) {
            Log.e("AuthRepository", "googleAuth() exception: ${e.message}", e)
            Response.error(400, "Error: ${e.message}".toResponseBody(null))
        }
    }

    suspend fun getCurrentUser(token: String): Response<AuthResponse> {
        if (cachedUser != null && !isCacheExpired()) {
            Log.d("AuthRepository", "getCurrentUser() returned cached user")
            return Response.success(AuthResponse(cachedUser!!, token))
        }

        return try {
            val response = apiService.getCurrentUser("Bearer $token")
            Log.d("AuthRepository", "getCurrentUser() response code: ${response.code()} body: ${response.body()} error: ${response.errorBody()?.string()}")
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    updateCache(authResponse.user, token)
                }
            }
            response
        } catch (e: Exception) {
            Log.e("AuthRepository", "getCurrentUser() exception: ${e.message}", e)
            clearCache()
            Response.error(400, "Error: ${e.message}".toResponseBody(null))
        }
    }

    suspend fun getToken(): String? = authPreferences.tokenFlow.first()

    private suspend fun updateCache(user: UserResponseDto, token: String) {
        cachedUser = user
        lastFetchTime = System.currentTimeMillis()
        authPreferences.saveAuthData(token, user.id)
        Log.d("AuthRepository", "Cache updated for user id: ${user.id}")
    }

    private fun clearCache() {
        cachedUser = null
        lastFetchTime = 0
        Log.d("AuthRepository", "Cache cleared")
    }

    private fun isCacheExpired(): Boolean {
        val expired = System.currentTimeMillis() - lastFetchTime > cacheTimeout
        Log.d("AuthRepository", "Cache expired: $expired")
        return expired
    }
}
package com.wheezy.myjetpackproject.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wheezy.myjetpackproject.Data.Dto.UserLoginDto
import com.wheezy.myjetpackproject.Data.Dto.UserRegisterDto
import com.wheezy.myjetpackproject.Data.Model.User
import com.wheezy.myjetpackproject.Data.Sealed.AuthState
import com.wheezy.myjetpackproject.Repository.AuthRepository
import com.wheezy.myjetpackproject.Utils.AuthPreferences
import com.wheezy.myjetpackproject.Utils.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState.asStateFlow()

    private val _googleAuthState = MutableStateFlow<AuthState>(AuthState.Idle)
    val googleAuthState: StateFlow<AuthState> = _googleAuthState.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private var initialUserFetchDone = false

    init {
        viewModelScope.launch {
            val isAuth = authPreferences.isAuthenticated()
            if (isAuth) {
                val token = authPreferences.tokenFlow.first() ?: ""
                fetchUserWithToken(token)
            } else {
                _user.value = null
            }
            initialUserFetchDone = true
        }

        viewModelScope.launch {
            authPreferences.tokenFlow
                .drop(1)
                .distinctUntilChanged()
                .collect { token ->
                    token?.let { fetchUserWithToken(it) } ?: run {
                        _user.value = null
                    }
                }
        }
    }

    private suspend fun fetchUserWithToken(token: String) {
        if (token.isBlank()) {
            clearUserData()
            return
        }

        try {
            val response = repository.getCurrentUser(token)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    _user.value = authResponse.user.toDomain()
                } ?: clearUserData()
            } else {
                clearUserData()
            }
        } catch (e: Exception) {
            clearUserData()
        }
    }

    private suspend fun clearUserData() {
        _user.value = null
        authPreferences.clearAuthData()
    }

    fun login(email: String, password: String) {
        if (_loginState.value is AuthState.Loading) return

        _loginState.value = AuthState.Loading
        Log.d("AuthViewModel", "🔑 Login started for email=$email")

        viewModelScope.launch {
            try {
                val response = repository.login(UserLoginDto(email, password))
                Log.d(
                    "AuthViewModel",
                    "📡 Login response: code=${response.code()}, body=${response.body()}"
                )

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authPreferences.saveAuthData(authResponse.token, authResponse.user.id)
                    _user.value = authResponse.user.toDomain()
                    _loginState.value = AuthState.Success(authResponse)
                    Log.d(
                        "AuthViewModel",
                        "✅ Login success: userId=${authResponse.user.id}, token=${
                            authResponse.token.take(20)
                        }..."
                    )
                } else {
                    val msg = response.message() ?: "Login failed"
                    _loginState.value = AuthState.Error(msg)
                    Log.w("AuthViewModel", "⚠️ Login error: $msg")
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(e.message ?: "Unknown error")
                Log.e("AuthViewModel", "💥 Login exception", e)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (_registerState.value is AuthState.Loading) return

        _registerState.value = AuthState.Loading
        Log.d("AuthViewModel", "📝 Registration started for email=$email, name=$name")

        viewModelScope.launch {
            try {
                val response = repository.register(UserRegisterDto(email, password, name))
                Log.d(
                    "AuthViewModel",
                    "📡 Register response: code=${response.code()}, body=${response.body()}"
                )

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authPreferences.saveAuthData(authResponse.token, authResponse.user.id)
                    _user.value = authResponse.user.toDomain()
                    _registerState.value = AuthState.Success(authResponse)
                    Log.d(
                        "AuthViewModel",
                        "✅ Registration success: userId=${authResponse.user.id}, token=${
                            authResponse.token.take(20)
                        }..."
                    )
                } else {
                    val msg = response.message() ?: "Registration failed"
                    _registerState.value = AuthState.Error(msg)
                    Log.w("AuthViewModel", "⚠️ Registration error: $msg")
                }
            } catch (e: Exception) {
                _registerState.value = AuthState.Error(e.message ?: "Unknown error")
                Log.e("AuthViewModel", "💥 Registration exception", e)
            }
        }
    }


    fun googleAuth(token: String) {
        if (_googleAuthState.value is AuthState.Loading) return

        _googleAuthState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = repository.googleAuth(token)
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authPreferences.saveAuthData(authResponse.token, authResponse.user.id)
                    _user.value = authResponse.user.toDomain()
                    _googleAuthState.value = AuthState.Success(authResponse)
                } else {
                    _googleAuthState.value = AuthState.Error(
                        response.message() ?: "Google authentication failed"
                    )
                }
            } catch (e: Exception) {
                _googleAuthState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authPreferences.clearAuthData()
            _user.value = null
            _loginState.value = AuthState.Idle
            _registerState.value = AuthState.Idle
            _googleAuthState.value = AuthState.Idle
        }
    }
}
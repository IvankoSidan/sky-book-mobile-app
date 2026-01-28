package com.wheezy.myjetpackproject.Repository

import android.content.Context
import com.wheezy.myjetpackproject.Network.ApiService
import com.wheezy.myjetpackproject.Utils.AuthPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        authPreferences: AuthPreferences
    ): AuthRepository = AuthRepository(apiService, authPreferences)

    @Singleton
    fun provideLocationRepository(
        apiService: ApiService
    ): LocationRepository = LocationRepository(apiService)

    @Singleton
    fun provideFlightRepository(
        apiService: ApiService,
        authRepository: AuthRepository
    ): FlightRepository = FlightRepository(apiService, authRepository)

    @Singleton
    fun providePaymentRepository(
        apiService: ApiService,
        authRepository: AuthRepository
    ): PaymentRepository = PaymentRepository(apiService, authRepository)

    @Singleton
    fun provideNotificationRepository(
        apiService: ApiService,
        authRepository: AuthRepository
    ): NotificationRepository = NotificationRepository(apiService, authRepository)

    @Singleton
    fun provideBookingRepository(
        apiService: ApiService,
        authRepository: AuthRepository
    ): BookingRepository = BookingRepository(apiService, authRepository)
}


package com.wheezy.myjetpackproject.data.di

import com.wheezy.myjetpackproject.core.network.ApiService
import com.wheezy.myjetpackproject.core.datastore.AuthPreferences
import com.wheezy.myjetpackproject.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        authPreferences: AuthPreferences
    ): AuthRepository = AuthRepository(apiService, authPreferences)

    @Provides
    @Singleton
    fun provideLocationRepository(
        apiService: ApiService
    ): LocationRepository = LocationRepository(apiService)

    @Provides
    @Singleton
    fun provideFlightRepository(
        apiService: ApiService,
        authRepository: AuthRepository
    ): FlightRepository = FlightRepository(apiService, authRepository)

    @Provides
    @Singleton
    fun providePaymentRepository(
        apiService: ApiService,
        authRepository: AuthRepository
    ): PaymentRepository = PaymentRepository(apiService, authRepository)

    @Provides
    @Singleton
    fun provideNotificationRepository(
        apiService: ApiService,
        authRepository: AuthRepository
    ): NotificationRepository = NotificationRepository(apiService, authRepository)

    @Provides
    @Singleton
    fun provideBookingRepository(
        apiService: ApiService,
        authRepository: AuthRepository
    ): BookingRepository = BookingRepository(apiService, authRepository)
}
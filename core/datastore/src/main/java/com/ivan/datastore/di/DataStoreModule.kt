package com.wheezy.myjetpackproject.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.wheezy.myjetpackproject.core.datastore.AuthPreferences
import com.wheezy.myjetpackproject.core.datastore.ThemePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ThemeDataStore

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    @AuthDataStore
    fun provideAuthDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile("auth_prefs") }
        )
    }

    @Provides
    @Singleton
    @ThemeDataStore
    fun provideThemeDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile("theme_prefs") }
        )
    }

    @Provides
    @Singleton
    fun provideAuthPreferences(@AuthDataStore dataStore: DataStore<Preferences>): AuthPreferences {
        return AuthPreferences(dataStore)
    }

    @Provides
    @Singleton
    fun provideThemePreferences(@ThemeDataStore dataStore: DataStore<Preferences>): ThemePreferences {
        return ThemePreferences(dataStore)
    }
}
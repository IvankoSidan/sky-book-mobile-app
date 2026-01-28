package com.wheezy.myjetpackproject.ViewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wheezy.myjetpackproject.Data.Enums.ThemeOption
import com.wheezy.myjetpackproject.UI.Components.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferences: ThemePreferences
) : ViewModel() {

    val currentTheme: StateFlow<ThemeOption> = preferences.currentTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ThemeOption.Auto
        )

    fun setTheme(theme: ThemeOption) {
        viewModelScope.launch {
            preferences.setTheme(theme)
        }
    }
}
package com.wheezy.myjetpackproject.common_vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wheezy.myjetpackproject.core.model.ThemeOption
import com.wheezy.myjetpackproject.core.datastore.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
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
package com.wheezy.myjetpackproject.search


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchParamsViewModel @Inject constructor() : ViewModel() {
    private val _from = MutableStateFlow("")
    val from: StateFlow<String> = _from

    private val _to = MutableStateFlow("")
    val to: StateFlow<String> = _to

    private val _numPassenger = MutableStateFlow(0)
    val numPassenger: StateFlow<Int> = _numPassenger

    fun setParams(from: String, to: String, numPassenger: Int) {
        _from.value = from
        _to.value = to
        _numPassenger.value = numPassenger
    }
}

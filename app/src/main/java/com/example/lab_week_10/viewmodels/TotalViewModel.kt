package com.example.lab_week_10.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TotalViewModel : ViewModel() {
    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total

    private val _lastUpdated = MutableLiveData<String>()
    val lastUpdated: LiveData<String> = _lastUpdated

    init {
        _total.value = 0
        _lastUpdated.value = "Never"
    }

    fun incrementTotal() {
        val current = _total.value ?: 0
        _total.value = current + 1
    }

    fun setTotal(newTotal: Int) {
        _total.value = newTotal
    }

    fun setLastUpdated(date: String) {
        _lastUpdated.value = date
    }
}
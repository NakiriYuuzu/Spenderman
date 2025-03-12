package net.yuuzu.spenderman.ui.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S, E>(initialState: S) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()
    
    protected fun updateState(update: (S) -> S) {
        _state.update(update)
    }
    
    abstract fun onEvent(event: E)
    
    fun onCleared() {
        viewModelScope.cancel()
    }
    
    protected fun getViewModelScope(): CoroutineScope = viewModelScope
}

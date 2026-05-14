package com.fahim.chatroom.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.auth.usecase.SignInUseCase
import com.fahim.chatroom.domain.auth.usecase.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signIn: SignInUseCase,
    private val signUp: SignUpUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun toggleMode() {
        _state.update { it.copy(mode = it.mode.opposite(), errorMessage = null) }
    }

    fun submit() {
        val current = _state.value
        if (!current.canSubmit) return
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = when (current.mode) {
                AuthUiState.Mode.SignIn -> signIn(current.email, current.password)
                AuthUiState.Mode.SignUp -> signUp(current.email, current.password)
            }
            when (result) {
                is AppResult.Success -> _state.update { it.copy(isLoading = false) }
                is AppResult.Failure -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.error.message ?: "Sign-in failed")
                }
            }
        }
    }
}
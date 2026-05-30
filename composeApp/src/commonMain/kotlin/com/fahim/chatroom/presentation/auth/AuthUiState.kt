package com.fahim.chatroom.presentation.auth

data class AuthUiState(
    val mode: Mode = Mode.SignIn,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() && password.length >= MIN_PASSWORD_LENGTH && !isLoading

    enum class Mode {
        SignIn, SignUp;

        fun opposite(): Mode = if (this == SignIn) SignUp else SignIn
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
package com.fahim.chatroom.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.fahim.chatroom.presentation.designsystem.components.AppScaffold
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    AuthContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::submit,
        onToggleMode = viewModel::toggleMode,
        modifier = modifier,
    )
}

@Composable
private fun AuthContent(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSignIn = state.mode == AuthUiState.Mode.SignIn

    AppScaffold(
        title = if (isSignIn) "Sign in" else "Create account",
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                enabled = !state.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                enabled = !state.isLoading,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                supportingText = {
                    Text(
                        "Minimum ${AuthUiState.MIN_PASSWORD_LENGTH} characters",
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            val errorText = state.errorMessage
            if (errorText != null) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Button(
                onClick = onSubmit,
                enabled = state.canSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(buttonLabel(state.mode, state.isLoading))
            }

            TextButton(
                onClick = onToggleMode,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (isSignIn) "Need an account? Create one" else "Have an account? Sign in")
            }
        }
    }
}

private fun buttonLabel(mode: AuthUiState.Mode, isLoading: Boolean): String = when {
    isLoading && mode == AuthUiState.Mode.SignIn -> "Signing in…"
    isLoading && mode == AuthUiState.Mode.SignUp -> "Creating account…"
    mode == AuthUiState.Mode.SignIn -> "Sign in"
    else -> "Create account"
}

@Preview
@Composable
private fun AuthScreenSignInPreview() {
    ChatTheme {
        AuthContent(
            state = AuthUiState(mode = AuthUiState.Mode.SignIn, email = "ada@example.com"),
            onEmailChange = {}, onPasswordChange = {}, onSubmit = {}, onToggleMode = {},
        )
    }
}

@Preview
@Composable
private fun AuthScreenSignUpPreview() {
    ChatTheme {
        AuthContent(
            state = AuthUiState(
                mode = AuthUiState.Mode.SignUp,
                email = "ada@example.com",
                password = "short",
                errorMessage = "Password must be at least 6 characters.",
            ),
            onEmailChange = {}, onPasswordChange = {}, onSubmit = {}, onToggleMode = {},
        )
    }
}

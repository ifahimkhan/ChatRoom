package com.fahim.chatroom.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.fahim.chatroom.presentation.designsystem.components.AppScaffold
import com.fahim.chatroom.presentation.designsystem.components.LoadingView
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    AppScaffold(
        title = "Profile",
        modifier = modifier,
        onBack = onBack,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                LoadingView()
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(text = state.email, style = MaterialTheme.typography.bodyLarge)

                    Spacer(Modifier.height(Spacing.sm))

                    OutlinedTextField(
                        value = state.displayNameDraft,
                        onValueChange = viewModel::onDisplayNameChange,
                        label = { Text("Display name") },
                        singleLine = true,
                        enabled = !state.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    val err = state.saveError
                    val savedMsg = state.savedMessage
                    when {
                        err != null -> Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        savedMsg != null -> Text(
                            text = savedMsg,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Button(
                        onClick = viewModel::save,
                        enabled = state.canSave,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (state.isSaving) "Saving…" else "Save")
                    }

                    Spacer(Modifier.weight(1f))

                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Sign out") }
                }
            }
        }
    }
}

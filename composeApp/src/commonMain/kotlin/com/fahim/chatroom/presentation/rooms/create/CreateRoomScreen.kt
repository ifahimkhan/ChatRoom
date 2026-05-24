package com.fahim.chatroom.presentation.rooms.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.fahim.chatroom.presentation.designsystem.components.AppScaffold
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateRoomScreen(
    onClose: () -> Unit,
    onCreated: (roomId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateRoomViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is CreateRoomViewModel.Event.Created) onCreated(event.roomId)
        }
    }

    CreateRoomContent(
        state = state,
        onClose = onClose,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onAddInvitee = viewModel::addInvitee,
        onRemoveInvitee = viewModel::removeInvitee,
        onSubmit = viewModel::submit,
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateRoomContent(
    state: CreateRoomUiState,
    onClose: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddInvitee: () -> Unit,
    onRemoveInvitee: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        title = "New room",
        modifier = modifier,
        onBack = onClose,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text("Room name") },
                singleLine = true,
                enabled = !state.isCreating,
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Invite members (optional)", style = MaterialTheme.typography.titleSmall)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                OutlinedTextField(
                    value = state.emailInput,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    enabled = !state.isCreating,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { onAddInvitee() }),
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = onAddInvitee,
                    enabled = state.canAddInvitee,
                ) { Text(if (state.isResolving) "…" else "Add") }
            }

            val resolveError = state.resolveError
            if (resolveError != null) {
                Text(
                    text = resolveError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (state.invitees.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    state.invitees.forEach { invitee ->
                        AssistChip(
                            onClick = { onRemoveInvitee(invitee.userId) },
                            label = { Text("${invitee.displayName} ✕") },
                            colors = AssistChipDefaults.assistChipColors(),
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            val createError = state.createError
            if (createError != null) {
                Text(
                    text = createError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Button(
                onClick = onSubmit,
                enabled = state.canCreate,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isCreating) "Creating…" else "Create room")
            }
        }
    }
}

@Preview
@Composable
private fun CreateRoomScreenEmptyPreview() {
    ChatTheme {
        CreateRoomContent(
            state = CreateRoomUiState(),
            onClose = {}, onNameChange = {}, onEmailChange = {},
            onAddInvitee = {}, onRemoveInvitee = {}, onSubmit = {},
        )
    }
}

@Preview
@Composable
private fun CreateRoomScreenWithInviteesPreview() {
    ChatTheme {
        CreateRoomContent(
            state = CreateRoomUiState(
                name = "Founders",
                invitees = listOf(
                    CreateRoomUiState.Invitee("1", "ada@example.com", "Ada Lovelace"),
                    CreateRoomUiState.Invitee("2", "grace@example.com", "Grace Hopper"),
                ),
            ),
            onClose = {}, onNameChange = {}, onEmailChange = {},
            onAddInvitee = {}, onRemoveInvitee = {}, onSubmit = {},
        )
    }
}

package com.hisuperaman.wallety.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun InputConfirmationDialog(
    showDialog: Boolean,
    title: String,
    message: String? = null,
    inputText: String = "",
    placeholder: String? = null,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    maxCharCounter: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    var localInput by remember { mutableStateOf(inputText) }

    LaunchedEffect(showDialog, inputText) {
        if (showDialog) localInput = inputText
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = title) },
            text = {
                Column {
                    if (message != null) Text(text = message)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = localInput,
                        onValueChange = {
                            val isDeleting = it.length < localInput.length

                            if (isDeleting || maxCharCounter == null || it.length <= maxCharCounter) {
                                localInput = it
                            }
                        },
                        placeholder = { Text(placeholder ?: "Enter value") },
                        singleLine = true,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = KeyboardActions(onDone = { onConfirm(localInput) }),
                        supportingText = if (maxCharCounter == null) null else ({
                            Text(
                                text = "${localInput.length}/${maxCharCounter}",
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        })
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm(localInput) }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
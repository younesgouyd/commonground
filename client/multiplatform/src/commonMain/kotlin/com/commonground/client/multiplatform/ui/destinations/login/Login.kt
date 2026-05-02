package com.commonground.client.multiplatform.ui.destinations.login


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi

interface LoginNavActions {
    fun toSignUp()
}

@Composable
fun Login(
    viewModel: LoginViewModel,
    navActions: LoginNavActions
) {
    val state by viewModel.state.collectAsState()
    AdaptiveUi(
        wide = { Wide(state, viewModel, navActions) },
        compact = { Compact(state, viewModel, navActions) }
    )
}

@Composable
private fun Wide(
    state: LoginState,
    vm: LoginViewModel,
    navActions: LoginNavActions
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Row(modifier = Modifier.fillMaxSize()) {
            BrandingPanel(
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                LoginForm(
                    modifier = Modifier.widthIn(max = 420.dp).padding(32.dp),
                    state = state,
                    vm = vm,
                    navActions = navActions
                )
            }
        }
    }
}

@Composable
private fun Compact(
    state: LoginState,
    vm: LoginViewModel,
    navActions: LoginNavActions
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CompactBranding()
            LoginForm(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                vm = vm,
                navActions = navActions
            )
        }
    }
}

@Composable
private fun BrandingPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CommonGround",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Find events. Meet people. Build connections.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CompactBranding() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "CommonGround",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
    state: LoginState,
    vm: LoginViewModel,
    navActions: LoginNavActions
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Log in",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.email,
            onValueChange = vm::onEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            singleLine = true,
            isError = state.emailError != null,
            supportingText = state.emailError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            enabled = !state.isSubmitting
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = vm::onPasswordChange,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = vm::togglePasswordVisibility) {
                    Icon(
                        imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = if (state.isPasswordVisible) "Hide password"
                        else "Show password"
                    )
                }
            },
            singleLine = true,
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { { Text(it) } },
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            enabled = !state.isSubmitting
        )

        if (state.generalError != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    text = state.generalError,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            onClick = vm::submit,
            enabled = state.isValid && !state.isSubmitting
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Log in", style = MaterialTheme.typography.titleMedium)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "New here?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = navActions::toSignUp) {
                Text("Create an account")
            }
        }
    }
}
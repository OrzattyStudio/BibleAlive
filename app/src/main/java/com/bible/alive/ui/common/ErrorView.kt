package com.bible.alive.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class ErrorType {
    NETWORK,
    LOADING,
    NOT_FOUND,
    GENERIC
}

@Composable
fun ErrorView(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    errorType: ErrorType = ErrorType.GENERIC,
    retryButtonText: String = "Reintentar"
) {
    val icon: ImageVector = when (errorType) {
        ErrorType.NETWORK -> Icons.Default.CloudOff
        ErrorType.NOT_FOUND -> Icons.Default.SearchOff
        else -> Icons.Default.Error
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(text = retryButtonText)
                }
            }
        }
    }
}

@Composable
fun CompactErrorView(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRetry
            ) {
                Text(text = "Reintentar")
            }
        }
    }
}

@Composable
fun NetworkErrorView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorView(
        message = "Error de conexión. Por favor, verifica tu conexión a internet e intenta de nuevo.",
        onRetry = onRetry,
        modifier = modifier,
        errorType = ErrorType.NETWORK
    )
}

@Composable
fun NotFoundView(
    message: String = "No se encontró el contenido solicitado",
    onAction: (() -> Unit)? = null,
    actionText: String = "Volver",
    modifier: Modifier = Modifier
) {
    ErrorView(
        message = message,
        onRetry = onAction,
        modifier = modifier,
        errorType = ErrorType.NOT_FOUND,
        retryButtonText = actionText
    )
}

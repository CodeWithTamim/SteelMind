package com.nasahacker.steelmind.compose.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    onNavigationClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors()
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAppBar() {
    AppBar(
        title = "Sample App Bar",
        onNavigationClick = {},
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Action"
                )
            }
        }
    )
}

package com.nasahacker.steelmind.compose.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    title: String,
) {
    var isExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        },
        actions = {
            IconButton(onClick = { isExpanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                IconTextRow(
                    text = "History",
                    imageVector = Icons.Default.AddCircle,
                    onClick = { isExpanded = false }
                )
                IconTextRow(
                    text = "Settings",
                    imageVector = Icons.Default.Settings,
                    onClick = { isExpanded = false }
                )
                IconTextRow(
                    text = "About",
                    imageVector = Icons.Default.Info,
                    onClick = { isExpanded = false }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors()
    )
}

@Composable
private fun IconTextRow(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewIconTextRow() {
    IconTextRow(
        text = "History",
        imageVector = Icons.Default.Info
    ) {}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMainAppBar() {
    MainAppBar(
        title = "Steel Mind"
    )
}

package com.nasahacker.steelmind.compose.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.nasahacker.steelmind.compose.ui.component.AppBar
import com.nasahacker.steelmind.compose.ui.component.DeleteDialog
import com.nasahacker.steelmind.compose.ui.component.HistoryItem
import com.nasahacker.steelmind.dto.History
import com.nasahacker.steelmind.util.MmkvManager

@Composable
fun HistoryScreen(onNavigationClick: () -> Unit) {
    // some states
    var historyList by remember { mutableStateOf(MmkvManager.getHistory()) }
    var isDeleteAllDialogVisible by remember { mutableStateOf(false) }
    var isDeleteItemDialogVisible by remember { mutableStateOf(false) }
    var selectedHistory: History? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        AppBar(
            title = "History",
            onNavigationClick = onNavigationClick,
            actions = {
                IconButton(onClick = { isDeleteAllDialogVisible = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete All History"
                    )
                }
            }
        )

        LazyColumn {
            items(historyList) { history ->
                HistoryItem(
                    action = history.action,
                    remarks = history.remarks,
                    time = history.time,
                    onClick = {

                    },
                    onLongPress = {
                        selectedHistory = history
                        isDeleteItemDialogVisible = true
                    }
                )
            }
        }

        if (isDeleteAllDialogVisible) {
            DeleteDialog(
                onDismissRequest = { isDeleteAllDialogVisible = false },
                onConfirmation = {
                    MmkvManager.clearAllHistory()
                    historyList = MmkvManager.getHistory()
                    isDeleteAllDialogVisible = false
                    Toast.makeText(context, "All history deleted successfully.", Toast.LENGTH_SHORT).show()
                },
                dialogTitle = "Delete All?",
                dialogText = "This action will delete all the history data. Confirm to proceed.",
                icon = Icons.Filled.Delete
            )
        }

        if (isDeleteItemDialogVisible) {
            selectedHistory?.let { history ->
                DeleteDialog(
                    onDismissRequest = { isDeleteItemDialogVisible = false },
                    onConfirmation = {
                        MmkvManager.deleteHistory(history)
                        historyList = MmkvManager.getHistory()
                        isDeleteItemDialogVisible = false
                        Toast.makeText(context, "Item deleted successfully.", Toast.LENGTH_SHORT).show()
                    },
                    dialogTitle = "Delete Item?",
                    dialogText = "This action will delete the selected item. Confirm to proceed.",
                    icon = Icons.Filled.Delete
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewHistoryScreen() {
    HistoryScreen(onNavigationClick = {})
}

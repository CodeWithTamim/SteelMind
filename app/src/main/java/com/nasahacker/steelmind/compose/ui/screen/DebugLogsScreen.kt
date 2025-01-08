package com.nasahacker.steelmind.compose.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nasahacker.steelmind.compose.ui.component.AppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


@Composable
fun DebugLogsScreen(onNavigationClick: () -> Unit) {

    val debugLogs = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()



    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec("logcat")
                val bufferReader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?
                while (bufferReader.readLine().also { line = it } != null) {
                    val log = line!!
                    withContext(Dispatchers.Main) {
                        debugLogs.add(log)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        AppBar(title = "Debug Logs", onNavigationClick = onNavigationClick, actions = {
            IconButton(onClick = {
                debugLogs.clear()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete, contentDescription = "Delete All Logs"
                )
            }
        })

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(debugLogs) { log ->
                Text(
                    text = log,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }


    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewDebugLogsScreen(modifier: Modifier = Modifier) {
    DebugLogsScreen(onNavigationClick = {

    })
}
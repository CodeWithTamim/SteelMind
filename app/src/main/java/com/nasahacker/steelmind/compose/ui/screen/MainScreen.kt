package com.nasahacker.steelmind.compose.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nasahacker.steelmind.compose.ui.component.AppBar
import com.nasahacker.steelmind.compose.ui.component.MainAppBar
import kotlinx.coroutines.MainScope

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    MainAppBar(
        title = "Steel Mind",
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMainScreen(modifier: Modifier = Modifier) {
    MainScreen()
}
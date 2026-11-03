package com.bodnar.carpathians

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.compose_multiplatform
import com.bodnar.carpathians.theme.CarpathiansBackground
import com.bodnar.carpathians.theme.CarpathiansTheme
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun App() {
    CarpathiansTheme {
        var showContent by remember { mutableStateOf(false) }
        CarpathiansBackground {
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Carpathians", style = MaterialTheme.typography.displayMedium)
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

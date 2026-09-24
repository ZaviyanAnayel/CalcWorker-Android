package com.zaviyanllc.calcworker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaviyanllc.calcworker.BuildConfig
import com.zaviyanllc.calcworker.data.CalcRegistry
import com.zaviyanllc.calcworker.ui.theme.Brand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text("🧮", style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(8.dp))
            Text("CalcWorker", style = MaterialTheme.typography.displaySmall)
            Text("by Zaviyan", style = MaterialTheme.typography.titleLarge, color = Brand.Sky)
            Spacer(Modifier.height(4.dp))
            Text("v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Text(
                "${CalcRegistry.all.size} calculators that work offline. Finance, tax, real estate, " +
                "health, math, currency and more — every formula runs on your device, no account, no tracking.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Card(shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Good to know", style = MaterialTheme.typography.titleMedium)
                    Text("• Calculators work fully offline.", style = MaterialTheme.typography.bodyMedium)
                    Text("• Currency rates refresh live when you're online, with an offline baseline as backup.", style = MaterialTheme.typography.bodyMedium)
                    Text("• CalcWorker AI answers formula questions; complex answers need the internet.", style = MaterialTheme.typography.bodyMedium)
                    Text("• Made with care by Zaviyan.", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

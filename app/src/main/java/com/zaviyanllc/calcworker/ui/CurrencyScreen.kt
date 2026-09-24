package com.zaviyanllc.calcworker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zaviyanllc.calcworker.data.CalcSpec
import com.zaviyanllc.calcworker.data.FxRepository
import com.zaviyanllc.calcworker.ui.theme.Brand
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    spec: CalcSpec,
    fx: FxRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val fxState by fx.state.collectAsState()
    var amount by remember { mutableStateOf("100") }
    var from by remember(spec.slug) { mutableStateOf(spec.currencyFrom ?: "USD") }
    var to by remember(spec.slug) { mutableStateOf(spec.currencyTo ?: "EUR") }

    LaunchedEffect(Unit) { scope.launch { fx.refresh() } }

    val amt = amount.toDoubleOrNull() ?: 0.0
    val result = fx.convert(amt, from, to)
    val rate = fx.rate(from, to)
    val nf = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2 } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(spec.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier.fillMaxSize().padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // live / cached badge
            AssistChip(
                onClick = { scope.launch { fx.refresh() } },
                label = { Text(if (fxState.live) "Live rates" else "Cached rates · Sep 2026") },
                leadingIcon = {
                    Icon(
                        if (fxState.live) Icons.Filled.CloudDone else Icons.Filled.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    leadingIconContentColor = if (fxState.live) Brand.Emerald else Brand.Gold
                )
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.matches(Regex("[0-9.,]*"))) amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = MaterialTheme.shapes.medium
            )

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CurrencyPicker("From", from, fx.currencies, fx, modifier = Modifier.weight(1f)) { from = it }
                IconButton(
                    onClick = { val t = from; from = to; to = t },
                    modifier = Modifier.size(48.dp)
                ) { Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap") }
                CurrencyPicker("To", to, fx.currencies, fx, modifier = Modifier.weight(1f)) { to = it }
            }

            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("$from → $to", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
                    Text(
                        "${fx.meta(to).first} ${nf.format(result)}",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "1 $from = ${nf.format(rate)} $to",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                    )
                    if (!fxState.live) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Offline baseline — connect to refresh live rates.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Brand.Gold
                        )
                    }
                }
            }

            if (spec.formula.isNotBlank()) {
                Text(spec.formula, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyPicker(
    label: String,
    value: String,
    codes: List<String>,
    fx: FxRepository,
    modifier: Modifier = Modifier,
    onPick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = "$value · ${fx.meta(value).second}",
            onValueChange = {}, readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            codes.forEach { code ->
                DropdownMenuItem(
                    text = { Text("$code · ${fx.meta(code).second}") },
                    onClick = { onPick(code); expanded = false },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

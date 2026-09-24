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
import com.zaviyanllc.calcworker.data.OmniCalcEngine
import com.zaviyanllc.calcworker.ui.theme.Brand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniScreen(spec: CalcSpec, onBack: () -> Unit) {
    var tab by remember { mutableStateOf(0) }
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
        Column(Modifier.fillMaxSize().padding(pad)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Calculator") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Converter") })
            }
            if (tab == 0) OmniCalculator() else OmniConverter()
        }
    }
}

@Composable
private fun OmniCalculator() {
    var expr by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    fun solve() {
        output = try {
            "= " + OmniCalcEngine.formatResult(OmniCalcEngine.evaluate(expr))
        } catch (_: Exception) { "Error — check the expression" }
    }
    val quick = listOf("7", "8", "9", "÷", "4", "5", "6", "×", "1", "2", "3", "-", "0", ".", "%", "+", "(", ")", "^", "√(", "π", "C", "⌫", "=")
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
            Column(Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Text(expr.ifEmpty { "0" }, style = MaterialTheme.typography.headlineMedium, maxLines = 3)
                if (output.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(output, style = MaterialTheme.typography.displaySmall, color = Brand.Sky)
                }
            }
        }
        // quick function row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("sin(", "cos(", "tan(", "ln(", "log(").forEach { f ->
                AssistChip(onClick = { expr += f }, label = { Text(f) })
            }
        }
        // keypad
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            quick.chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { key ->
                        val isOp = key in setOf("÷", "×", "-", "+", "=", "%", "^")
                        Button(
                            onClick = {
                                when (key) {
                                    "C" -> { expr = ""; output = "" }
                                    "⌫" -> expr = expr.dropLast(1)
                                    "=" -> solve()
                                    "√(" -> expr += "sqrt("
                                    else -> expr += key
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = if (isOp) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                     else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) { Text(key, style = MaterialTheme.typography.titleLarge) }
                    }
                }
            }
        }
        Text(
            "Tip: type any expression — e.g. (2500*1.08^5)+sqrt(144). Graphing arrives in v2.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(80.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OmniConverter() {
    val cats = OmniCalcEngine.categories
    var catKey by remember { mutableStateOf(cats.firstOrNull()?.key ?: "length") }
    val cat = cats.firstOrNull { it.key == catKey } ?: return
    var value by remember { mutableStateOf("1") }
    var from by remember(catKey) { mutableStateOf(cat.units.first().code) }
    var to by remember(catKey) { mutableStateOf(cat.units.getOrNull(1)?.code ?: cat.units.first().code) }

    val v = value.toDoubleOrNull() ?: 0.0
    val result = OmniCalcEngine.convert(v, catKey, from, to)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        var catExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = cat.name, onValueChange = {}, readOnly = true, label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(), shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                cats.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.name) },
                        onClick = { catKey = c.key; catExpanded = false },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = { if (it.matches(Regex("[0-9.,-]*"))) value = it },
            label = { Text("Value") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = MaterialTheme.shapes.medium
        )

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UnitPicker("From", from, cat.units.map { it.code to it.name }, Modifier.weight(1f)) { from = it }
            IconButton(onClick = { val t = from; from = to; to = t }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap")
            }
            UnitPicker("To", to, cat.units.map { it.code to it.name }, Modifier.weight(1f)) { to = it }
        }

        Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                Text("Result", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
                Text(
                    OmniCalcEngine.formatResult(result) + " " + (cat.units.firstOrNull { it.code == to }?.name ?: to),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitPicker(label: String, value: String, options: List<Pair<String, String>>, modifier: Modifier = Modifier, onPick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = options.firstOrNull { it.first == value }?.second ?: value,
            onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { onPick(code); expanded = false },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

package com.zaviyanllc.calcworker.ui

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zaviyanllc.calcworker.calc.Inp
import com.zaviyanllc.calcworker.data.CalcDispatcher
import com.zaviyanllc.calcworker.data.CalcInput
import com.zaviyanllc.calcworker.data.CalcRegistry
import com.zaviyanllc.calcworker.data.InputKind
import com.zaviyanllc.calcworker.data.PrefsRepository
import com.zaviyanllc.calcworker.ui.theme.Brand
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcScreen(
    slug: String,
    prefs: PrefsRepository,
    onBack: () -> Unit
) {
    val spec = CalcRegistry.bySlug[slug] ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Calculator not found") }
        return
    }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val favorites by prefs.favorites.collectAsState(initial = emptySet())
    val isFav = slug in favorites

    LaunchedEffect(slug) { prefs.pushRecent(slug) }

    // input state, survives rotation
    val mapSaver = Saver<SnapshotStateMap<String, String>, HashMap<String, String>>(
        save = { HashMap(it) },
        restore = { mutableStateMapOf<String, String>().apply { putAll(it) } }
    )
    val values = rememberSaveable(slug, saver = mapSaver) { mutableStateMapOf() }
    LaunchedEffect(slug) {
        spec.inputs.forEach { inp ->
            if (inp.id !in values) {
                values[inp.id] = when {
                    inp.def.isNotEmpty() -> inp.def
                    inp.kind == InputKind.SELECT && inp.options.isNotEmpty() -> inp.options.first().value
                    else -> ""
                }
            }
        }
    }

    val valuesSnapshot by remember { derivedStateOf { values.toMap() } }
    val results = remember(valuesSnapshot) {
        try {
            CalcDispatcher.run(slug, Inp(HashMap(valuesSnapshot)))
        } catch (_: Exception) { linkedMapOf() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(spec.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch { prefs.toggleFavorite(slug) }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            if (isFav) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) Brand.Gold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            val text = buildString {
                                append(spec.title).append("\n")
                                results.forEach { (id, v) ->
                                    val label = spec.outputs.firstOrNull { it.id == id }?.label ?: id
                                    append(label).append(": ").append(v).append("\n")
                                }
                                append("— via CalcWorker")
                            }
                            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text)
                            }, "Share result"))
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Share results")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (spec.blurb.isNotBlank()) {
                Text(spec.blurb, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            spec.inputs.forEach { inp ->
                when (inp.kind) {
                    InputKind.SLIDER -> SliderInput(inp, values)
                    else -> FieldInput(inp, values)
                }
            }

            // ---- animated result card ----
            AnimatedVisibility(
                visible = results.isNotEmpty(),
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top) + slideInVertically { -24 },
                exit = fadeOut() + shrinkVertically()
            ) {
                ResultCard(spec, results, onCopy = { label, value ->
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    clipboard.setText(AnnotatedString("$label: $value"))
                })
            }

            // ---- how it works ----
            if (spec.formula.isNotBlank() || spec.proTip.isNotBlank()) {
                var expanded by remember { mutableStateOf(false) }
                Card(
                    onClick = { expanded = !expanded },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = Brand.Sky)
                            Spacer(Modifier.width(8.dp))
                            Text("How it works", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                        }
                        AnimatedVisibility(visible = expanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                            Column {
                                if (spec.formula.isNotBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(spec.formula, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (spec.proTip.isNotBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text("Tip: ${spec.proTip}", style = MaterialTheme.typography.bodyMedium, color = Brand.Emerald)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ResultCard(
    spec: com.zaviyanllc.calcworker.data.CalcSpec,
    results: LinkedHashMap<String, String>,
    onCopy: (String, String) -> Unit
) {
    val shown = spec.outputs.mapNotNull { o -> results[o.id]?.let { o to it } }
    if (shown.isEmpty()) return
    val (primary, rest) = shown.first() to shown.drop(1)
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(primary.first.label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
                    Text(primary.second, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                IconButton(onClick = { onCopy(primary.first.label, primary.second) }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy result", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            rest.forEach { (o, v) ->
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(o.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), modifier = Modifier.weight(1f))
                    Text(v, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldInput(inp: CalcInput, values: SnapshotStateMap<String, String>) {
    val v = values[inp.id] ?: ""
    fun set(s: String) { values[inp.id] = s }
    when (inp.kind) {
        InputKind.NUMBER -> OutlinedTextField(
            value = v, onValueChange = { if (it.matches(Regex("[0-9.,-]*"))) set(it) },
            label = { Text(inp.label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            prefix = inp.prefix?.let { { Text(it) } },
            suffix = inp.suffix?.let { { Text(it) } },
            shape = MaterialTheme.shapes.medium
        )
        InputKind.SELECT -> {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = inp.options.firstOrNull { it.value == v }?.label ?: v,
                    onValueChange = {}, readOnly = true,
                    label = { Text(inp.label) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    inp.options.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt.label) },
                            onClick = { set(opt.value); expanded = false },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
        InputKind.SWITCH -> Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(inp.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Switch(checked = v == "true", onCheckedChange = { set(if (it) "true" else "false") })
        }
        InputKind.DATE -> {
            var showPicker by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = v, onValueChange = { set(it) },
                label = { Text(inp.label) },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showPicker = true }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Pick date")
                    }
                },
                shape = MaterialTheme.shapes.medium
            )
            if (showPicker) {
                val cal = remember { Calendar.getInstance() }
                val state = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            state.selectedDateMillis?.let { millis ->
                                cal.timeInMillis = millis
                                set("%04d-%02d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
                            }
                            showPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
                ) { DatePicker(state) }
            }
        }
        InputKind.TEXTAREA -> OutlinedTextField(
            value = v, onValueChange = { set(it) },
            label = { Text(inp.label) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            shape = MaterialTheme.shapes.medium
        )
        else -> OutlinedTextField(
            value = v, onValueChange = { set(it) },
            label = { Text(inp.label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            prefix = inp.prefix?.let { { Text(it) } },
            suffix = inp.suffix?.let { { Text(it) } },
            shape = MaterialTheme.shapes.medium
        )
    }
}

@Composable
private fun SliderInput(inp: CalcInput, values: SnapshotStateMap<String, String>) {
    val target = inp.bindTo ?: inp.id
    val raw = values[target]?.toDoubleOrNull()
    val min = inp.min ?: 0.0
    val max = inp.max ?: 100.0
    val cur = raw?.coerceIn(min, max) ?: (inp.def.toDoubleOrNull() ?: min)
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(inp.label, style = MaterialTheme.typography.bodyLarge)
            Text(
                (if (cur % 1.0 == 0.0) cur.toInt().toString() else "%.1f".format(cur)) + (inp.suffix ?: ""),
                style = MaterialTheme.typography.titleMedium,
                color = Brand.Sky
            )
        }
        Slider(
            value = cur.toFloat(),
            onValueChange = {
                val stepped = inp.step?.let { st -> kotlin.math.round(it / st) * st } ?: it.toDouble()
                values[target] = if (stepped % 1.0 == 0.0) stepped.toInt().toString() else "%.2f".format(stepped)
            },
            valueRange = min.toFloat()..max.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

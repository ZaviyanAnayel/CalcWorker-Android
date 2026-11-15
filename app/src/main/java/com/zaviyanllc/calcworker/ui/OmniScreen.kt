package com.zaviyanllc.calcworker.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                title = { Text(spec.title, maxLines = 1, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            TabRow(
                selectedTabIndex = tab,
                containerColor = Color(0xFF0B132B),
                contentColor = Brand.Sky
            ) {
                Tab(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    text = { Text("Calculator", fontWeight = if (tab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    text = { Text("Converter", fontWeight = if (tab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
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
        } catch (_: Exception) {
            "Error — check formula"
        }
    }

    val quick = listOf(
        "C", "⌫", "%", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "^", "="
    )

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Luxury Display Card ---
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF10B981)))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expr.ifEmpty { "0" },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = if (expr.isEmpty()) Color(0xFF64748B) else Color(0xFFF8FAFC),
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = output.ifEmpty { "Ready" },
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = if (output.isEmpty()) Color(0xFF475569) else Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                )
            }
        }

        // --- Scientific Quick Row ---
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("sin(", "cos(", "tan(", "ln(", "log(").forEach { f ->
                Button(
                    onClick = { expr += f },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color(0xFF38BDF8)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(f, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // --- Keypad Grid ---
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            quick.chunked(4).forEach { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { key ->
                        val isOp = key in setOf("÷", "×", "-", "+", "%", "^")
                        val isEquals = key == "="
                        val isClear = key == "C"
                        val isBack = key == "⌫"

                        val containerColor = when {
                            isEquals -> Color(0xFF10B981) // Emerald Green
                            isClear -> Color(0xFFE11D48)  // Crimson Red
                            isBack -> Color(0xFF334155)   // Muted Slate
                            isOp -> Color(0xFF0284C7)     // Vivid Sky Blue
                            else -> Color(0xFF1E293B)     // High-Contrast Dark Slate for digits
                        }

                        val textColor = when {
                            isEquals -> Color.White
                            isClear -> Color.White
                            isOp -> Color.White
                            else -> Color(0xFFF8FAFC)     // Crisp Pure White for numbers
                        }

                        Button(
                            onClick = {
                                when (key) {
                                    "C" -> { expr = ""; output = "" }
                                    "⌫" -> if (expr.isNotEmpty()) expr = expr.dropLast(1)
                                    "=" -> solve()
                                    else -> expr += key
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = containerColor,
                                contentColor = textColor
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = if (key.length > 1) 18.sp else 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Tip: Type any expression — e.g. (2500*1.08^5)+sqrt(144). Instant math engine.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B)
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
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        var catExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = catExpanded,
            onExpandedChange = { catExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = cat.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Brand.Sky,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A)
                )
            )
            ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                cats.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.name, fontWeight = FontWeight.Medium) },
                        onClick = { catKey = c.key; catExpanded = false },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = { if (it.matches(Regex("[0-9.,-]*"))) value = it },
            label = { Text("Enter Amount / Value") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Brand.Sky,
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A)
            )
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UnitPicker("From", from, cat.units.map { it.code to it.name }, Modifier.weight(1f)) { from = it }
            IconButton(
                onClick = { val t = from; from = to; to = t },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF1E293B), CircleShape)
            ) {
                Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap", tint = Brand.Sky)
            }
            UnitPicker("To", to, cat.units.map { it.code to it.name }, Modifier.weight(1f)) { to = it }
        }

        // --- Premium Converted Result Card ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Brand.Sky, Brand.Emerald))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "CONVERTED RESULT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = OmniCalcEngine.formatResult(result) + " " + (cat.units.firstOrNull { it.code == to }?.name ?: to),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                )
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitPicker(
    label: String,
    value: String,
    options: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    onPick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = options.firstOrNull { it.first == value }?.second ?: value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Brand.Sky,
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A)
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name, fontWeight = FontWeight.Medium) },
                    onClick = { onPick(code); expanded = false },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

package com.zaviyanllc.calcworker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zaviyanllc.calcworker.data.CalcRegistry
import com.zaviyanllc.calcworker.data.PrefsRepository
import com.zaviyanllc.calcworker.ui.components.*
import com.zaviyanllc.calcworker.ui.theme.Brand
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    prefs: PrefsRepository,
    onOpenCalc: (String) -> Unit,
    onOpenAi: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val favorites by prefs.favorites.collectAsState(initial = emptySet())
    val recents by prefs.recents.collectAsState(initial = emptyList())
    val theme by prefs.theme.collectAsState(initial = "dark")
    var query by remember { mutableStateOf("") }
    var chip by remember { mutableStateOf<String?>(null) }

    val results = remember(query, chip) {
        when {
            query.isNotBlank() -> CalcRegistry.search(query, 40)
            chip != null -> CalcRegistry.all.filter { it.category == chip }
            else -> emptyList()
        }
    }
    val filtering = query.isNotBlank() || chip != null
    val recentSpecs = remember(recents) { recents.mapNotNull { CalcRegistry.bySlug[it] } }
    val favSpecs = remember(favorites) { favorites.mapNotNull { CalcRegistry.bySlug[it] } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // ---- gradient hero ----
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Brand.Sky.copy(alpha = 0.22f),
                                Brand.Violet.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("CalcWorker", style = MaterialTheme.typography.displaySmall)
                        Row {
                            IconButton(onClick = { onOpenAi() }, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = "Ask AI", tint = Brand.Sky)
                            }
                            IconButton(
                                onClick = { scope.launch { prefs.setTheme(if (theme == "dark") "dim" else "dark") } },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    if (theme == "dark") Icons.Filled.DarkMode else Icons.Filled.BrightnessMedium,
                                    contentDescription = if (theme == "dark") "Switch to Dim theme" else "Switch to Dark theme",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Text(
                        "${CalcRegistry.all.size} calculators · offline-first",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    // ---- live search ----
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search calculators…  (try “m”)") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        trailingIcon = {
                            if (query.isNotEmpty()) IconButton(onClick = { query = "" }, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        },
                        singleLine = true,
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )
                }
            }
        }

        // ---- category chips ----
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CalcRegistry.categories) { cat ->
                    FilterChip(
                        selected = chip == cat,
                        onClick = { chip = if (chip == cat) null else cat },
                        label = { Text(cat) }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        if (filtering) {
            if (results.isEmpty()) {
                item { EmptyHint(Icons.Filled.SearchOff, "No calculators match. Try another word.") }
            } else {
                item { SectionHeader("${results.size} result${if (results.size == 1) "" else "s"}") }
                items(results, key = { it.slug }) { spec ->
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        CalcRowCard(
                            spec = spec,
                            isFavorite = spec.slug in favorites,
                            onOpen = { onOpenCalc(spec.slug) },
                            onToggleFavorite = { scope.launch { prefs.toggleFavorite(spec.slug) } }
                        )
                    }
                }
            }
        } else {
            // ---- popular ----
            item { SectionHeader("Popular now") }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(CalcRegistry.popular, key = { it.slug }) { spec ->
                        CalcMiniCard(spec, onOpen = { onOpenCalc(spec.slug) })
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ---- favorites ----
            if (favSpecs.isNotEmpty()) {
                item { SectionHeader("Your favorites") }
                items(favSpecs, key = { it.slug }) { spec ->
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        CalcRowCard(
                            spec, isFavorite = true,
                            onOpen = { onOpenCalc(spec.slug) },
                            onToggleFavorite = { scope.launch { prefs.toggleFavorite(spec.slug) } }
                        )
                    }
                }
            }

            // ---- recent ----
            if (recentSpecs.isNotEmpty()) {
                item { SectionHeader("Recently used") }
                items(recentSpecs.take(6), key = { it.slug }) { spec ->
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        CalcRowCard(
                            spec, isFavorite = spec.slug in favorites,
                            onOpen = { onOpenCalc(spec.slug) },
                            onToggleFavorite = { scope.launch { prefs.toggleFavorite(spec.slug) } }
                        )
                    }
                }
            }

            // ---- browse all by category ----
            CalcRegistry.categories.forEach { cat ->
                val inCat = CalcRegistry.all.filter { it.category == cat }
                if (inCat.isNotEmpty()) {
                    item { SectionHeader("$cat (${inCat.size})") }
                    items(inCat, key = { it.slug }) { spec ->
                        Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                            CalcRowCard(
                                spec, isFavorite = spec.slug in favorites,
                                onOpen = { onOpenCalc(spec.slug) },
                                onToggleFavorite = { scope.launch { prefs.toggleFavorite(spec.slug) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

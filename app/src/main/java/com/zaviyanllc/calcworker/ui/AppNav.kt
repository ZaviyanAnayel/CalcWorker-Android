package com.zaviyanllc.calcworker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zaviyanllc.calcworker.data.AiRepository
import com.zaviyanllc.calcworker.data.CalcKind
import com.zaviyanllc.calcworker.data.CalcRegistry
import com.zaviyanllc.calcworker.data.FxRepository
import com.zaviyanllc.calcworker.data.PrefsRepository
import com.zaviyanllc.calcworker.ui.components.CalcRowCard
import com.zaviyanllc.calcworker.ui.components.EmptyHint
import kotlinx.coroutines.launch

@Composable
fun AppNav(
    prefs: PrefsRepository,
    fx: FxRepository,
    ai: AiRepository,
    nav: NavHostController = rememberNavController()
) {
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: "home"
    val showBottomBar = route in listOf("home", "favorites", "ai", "about")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    listOf(
                        Triple("home", "Home", Icons.Filled.Home),
                        Triple("favorites", "Favorites", Icons.Filled.Star),
                        Triple("ai", "AI", Icons.Filled.AutoAwesome),
                        Triple("about", "About", Icons.Filled.Info)
                    ).forEach { (r, label, icon) ->
                        NavigationBarItem(
                            selected = route == r,
                            onClick = { if (route != r) nav.navigate(r) { launchSingleTop = true } },
                            icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            NavHost(navController = nav, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        prefs = prefs,
                        onOpenCalc = { nav.navigate("calc/$it") },
                        onOpenAi = { nav.navigate("ai") }
                    )
                }
                composable("favorites") {
                    FavoritesScreen(prefs = prefs, onOpenCalc = { nav.navigate("calc/$it") })
                }
                composable("ai") {
                    AiScreen(ai = ai, onOpenCalc = { nav.navigate("calc/$it") }, onBack = { nav.popBackStack() })
                }
                composable("about") { AboutScreen(onBack = { nav.popBackStack() }) }
                composable("calc/{slug}") { entry ->
                    val slug = entry.arguments?.getString("slug") ?: return@composable
                    val spec = CalcRegistry.bySlug[slug]
                    when (spec?.kind) {
                        CalcKind.CURRENCY -> CurrencyScreen(spec = spec, fx = fx, onBack = { nav.popBackStack() })
                        CalcKind.OMNICALC -> OmniScreen(spec = spec, onBack = { nav.popBackStack() })
                        else -> CalcScreen(slug = slug, prefs = prefs, onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesScreen(prefs: PrefsRepository, onOpenCalc: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val favorites by prefs.favorites.collectAsState(initial = emptySet())
    val specs = remember(favorites) { favorites.mapNotNull { CalcRegistry.bySlug[it] } }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Text(
                "Favorites",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        if (specs.isEmpty()) {
            item { EmptyHint(Icons.Filled.StarBorder, "Tap the star on any calculator to pin it here.") }
        } else {
            items(specs, key = { it.slug }) { spec ->
                Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    CalcRowCard(
                        spec, isFavorite = true,
                        onOpen = { onOpenCalc(spec.slug) },
                        onToggleFavorite = { scope.launch { prefs.toggleFavorite(spec.slug) } }
                    )
                }
            }
        }
    }
}

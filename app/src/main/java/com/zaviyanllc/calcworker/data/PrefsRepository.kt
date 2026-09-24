package com.zaviyanllc.calcworker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "calcworker_prefs")

/** Favorites, recents and theme — the only things the app persists. */
class PrefsRepository(private val context: Context) {

    private object Keys {
        val FAVORITES = stringSetPreferencesKey("favorites")
        val RECENTS = stringPreferencesKey("recents_csv")
        val THEME = stringPreferencesKey("theme") // "dark" | "dim"
    }

    val favorites: Flow<Set<String>> = context.dataStore.data
        .map { it[Keys.FAVORITES] ?: emptySet() }

    val recents: Flow<List<String>> = context.dataStore.data
        .map { (it[Keys.RECENTS] ?: "").split(",").filter { s -> s.isNotBlank() } }

    val theme: Flow<String> = context.dataStore.data
        .map { it[Keys.THEME] ?: "dark" }

    suspend fun toggleFavorite(slug: String) {
        context.dataStore.edit { prefs ->
            val cur = prefs[Keys.FAVORITES] ?: emptySet()
            prefs[Keys.FAVORITES] = if (slug in cur) cur - slug else cur + slug
        }
    }

    suspend fun pushRecent(slug: String) {
        context.dataStore.edit { prefs ->
            val cur = (prefs[Keys.RECENTS] ?: "").split(",").filter { it.isNotBlank() }.toMutableList()
            cur.remove(slug)
            cur.add(0, slug)
            prefs[Keys.RECENTS] = cur.take(12).joinToString(",")
        }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[Keys.THEME] = theme }
    }

    suspend fun isFavorite(slug: String): Boolean = favorites.first().contains(slug)
}

package com.zaviyanllc.calcworker.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Favorites, recents and theme — rock-solid SharedPreferences with StateFlow.
 *  Zero disk lock, zero protobuf, zero background thread deadlocks. */
class PrefsRepository(context: Context) {

    private val sp: SharedPreferences = context.applicationContext.getSharedPreferences("calcworker_prefs", Context.MODE_PRIVATE)

    private val _favorites = MutableStateFlow(loadFavorites())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _recents = MutableStateFlow(loadRecents())
    val recents: StateFlow<List<String>> = _recents.asStateFlow()

    private val _theme = MutableStateFlow(sp.getString("theme", "dark") ?: "dark")
    val theme: StateFlow<String> = _theme.asStateFlow()

    private fun loadFavorites(): Set<String> {
        return sp.getStringSet("favorites", null) ?: emptySet()
    }

    private fun loadRecents(): List<String> {
        val raw = sp.getString("recents_csv", "") ?: ""
        return raw.split(",").filter { it.isNotBlank() }
    }

    suspend fun toggleFavorite(slug: String) {
        val cur = _favorites.value
        val updated = if (slug in cur) cur - slug else cur + slug
        _favorites.value = updated
        sp.edit().putStringSet("favorites", updated).apply()
    }

    suspend fun pushRecent(slug: String) {
        val cur = _recents.value.toMutableList()
        cur.remove(slug)
        cur.add(0, slug)
        val updated = cur.take(12)
        _recents.value = updated
        sp.edit().putString("recents_csv", updated.joinToString(",")).apply()
    }

    suspend fun setTheme(theme: String) {
        _theme.value = theme
        sp.edit().putString("theme", theme).apply()
    }
}

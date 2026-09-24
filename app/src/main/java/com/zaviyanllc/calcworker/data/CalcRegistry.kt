package com.zaviyanllc.calcworker.data

import com.zaviyanllc.calcworker.data.gen.AiCatalogData
import com.zaviyanllc.calcworker.data.gen.SpecData

/** Central registry: every calculator the app ships, its category and search index. */
object CalcRegistry {
    val all: List<CalcSpec> = SpecData.specs
    val bySlug: Map<String, CalcSpec> = all.associateBy { it.slug }

    val categories: List<String> = listOf(
        "Finance & Loans", "Tax", "Real Estate", "Business",
        "Creator", "Wealth", "Health & Lifestyle", "Currency", "Science & Math"
    ).filter { c -> all.any { it.category == c } }

    val popular: List<CalcSpec> = all.filter { it.popular }

    /** Lightweight full-text search over titles + keywords (first character is enough). */
    fun search(query: String, limit: Int = 12): List<CalcSpec> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        val scored = all.mapNotNull { spec ->
            var score = 0
            val title = spec.title.lowercase()
            when {
                title == q -> score = 100
                title.startsWith(q) -> score = 80
                title.contains(q) -> score = 50
                spec.slug.replace("-", " ").contains(q) -> score = 40
                else -> {
                    val hit = spec.keywords.indexOfFirst { it.lowercase().contains(q) }
                    if (hit >= 0) score = 30 - hit
                }
            }
            if (score > 0) spec to score else null
        }
        return scored.sortedByDescending { it.second }.take(limit).map { it.first }
    }

    /** Keyword match used by the offline AI helper: best calculators for a question. */
    fun matchCalculators(query: String, limit: Int = 3): List<AiEntry> {
        val words = query.lowercase().split(Regex("[^a-z0-9]+")).filter { it.length > 2 }.toSet()
        if (words.isEmpty()) return emptyList()
        return AiCatalogData.entries.mapNotNull { e ->
            var score = 0
            val hayTitle = e.title.lowercase()
            for (w in words) {
                if (hayTitle.contains(w)) score += 3
                for (kw in e.keywords) {
                    val kl = kw.lowercase()
                    if (kl == w) score += 4 else if (kl.contains(w)) score += 2
                }
            }
            if (score > 0) e to score else null
        }.sortedByDescending { it.second }.take(limit).map { it.first }
    }
}

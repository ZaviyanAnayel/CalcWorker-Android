package com.zaviyanllc.calcworker.data

import com.zaviyanllc.calcworker.data.gen.FxData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** FX rates: Sep-2026 baked-in baseline, upgraded to the live feed when online.
 *  Mirrors the website's js/currency-engine.js behavior (no API key needed). */
class FxRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    data class FxState(
        val rates: Map<String, Double> = FxData.baseline,
        val live: Boolean = false,
        val updatedAt: String? = null
    )

    private val _state = MutableStateFlow(FxState())
    val state: StateFlow<FxState> = _state

    val currencies: List<String> = FxData.baseline.keys.sorted()

    fun meta(code: String): Pair<String, String> = FxData.meta[code] ?: ("" to code)

    /** Amount of `to` you get for 1 unit of `from`. */
    fun rate(from: String, to: String): Double {
        val r = _state.value.rates
        val rf = r[from] ?: return 0.0
        val rt = r[to] ?: return 0.0
        return if (rf == 0.0) 0.0 else rt / rf
    }

    fun convert(amount: Double, from: String, to: String): Double = amount * rate(from, to)

    /** Try the live feed; keep the baseline on any failure (offline-first). */
    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            try {
                val req = Request.Builder()
                    .url("https://open.er-api.com/v6/latest/USD")
                    .header("User-Agent", "CalcWorker-Android/2.0")
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@withContext
                    val body = resp.body?.string() ?: return@withContext
                    val json = JSONObject(body)
                    if (json.optString("result") != "success") return@withContext
                    val ratesJson = json.getJSONObject("rates")
                    val fresh = mutableMapOf<String, Double>()
                    val it = ratesJson.keys()
                    while (it.hasNext()) {
                        val k = it.next()
                        val v = ratesJson.optDouble(k, Double.NaN)
                        if (!v.isNaN() && v > 0) fresh[k] = v
                    }
                    if (fresh.size > 20) {
                        fresh["USD"] = 1.0
                        _state.value = FxState(
                            rates = fresh,
                            live = true,
                            updatedAt = json.optString("time_last_update_utc", "").ifEmpty { null }
                        )
                    }
                }
            } catch (_: Exception) {
                // stay on the cached baseline — offline is a first-class state
            }
        }
    }
}

package com.zaviyanllc.calcworker.calc

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow

/** Input bag: values keyed by input id. Numbers, strings, booleans. */
class Inp(val vals: Map<String, Any?> = emptyMap()) {
    fun num(key: String): Double = J.dbl(vals[key])
    fun int(key: String): Int = num(key).toInt()
    fun str(key: String): String = J.toStr(vals[key])
    fun bool(key: String): Boolean = when (val v = vals[key]) {
        is Boolean -> v
        is String -> v.equals("true", ignoreCase = true) || v == "1"
        is Number -> v.toDouble() != 0.0
        else -> false
    }
}

/** JS-math compatibility helpers used by transpiled formulas. */
object J {
    fun pf(s: String): Double = s.trim().toDoubleOrNull() ?: Double.NaN
    fun pf(x: Double): Double = x

    /** JS parseInt: leading integer prefix (returns Double to mirror JS number). */
    fun piD(s: String, radix: Int = 10): Double {
        val m = Regex("""^\s*[+-]?\d+""").find(s)
        return m?.value?.toDoubleOrNull() ?: Double.NaN
    }

    fun piD(x: Double): Double = if (x.isNaN()) Double.NaN else x.toLong().toDouble()

    fun toNum(x: Any?): Double = when (x) {
        is Number -> x.toDouble()
        is String -> x.trim().toDoubleOrNull() ?: Double.NaN
        is Boolean -> if (x) 1.0 else 0.0
        else -> Double.NaN
    }

    fun toStr(x: Any?): String = when (x) {
        null -> ""
        is Double -> s(x)
        is Float -> s(x.toDouble())
        else -> x.toString()
    }
    fun isFin(x: Double): Boolean = x.isFinite()

    fun dbl(x: Any?): Double = when (x) {
        is Number -> x.toDouble()
        is String -> x.trim().toDoubleOrNull() ?: 0.0
        is Boolean -> if (x) 1.0 else 0.0
        else -> 0.0
    }

    /** JS Math.round: rounds half up (unlike Kotlin's half-to-even). */
    fun round(x: Number): Double = floor(x.toDouble() + 0.5)

    fun pw(a: Double, b: Double): Double = a.pow(b)
    fun min(a: Number, b: Number): Double = kotlin.math.min(a.toDouble(), b.toDouble())
    fun max(a: Number, b: Number): Double = kotlin.math.max(a.toDouble(), b.toDouble())

    /** JS || for numbers: 0/NaN falls through to default. */
    fun orD(a: Double, b: Double): Double = if (a == 0.0 || a.isNaN()) b else a
    fun orD(a: Double, b: Int): Double = orD(a, b.toDouble())
    fun orD(a: Boolean, b: Boolean): Boolean = a || b
    // JS truthiness for ternary/if conditions
    fun truthy(x: Any?): Boolean = when (x) {
        null -> false
        is Boolean -> x
        is Number -> { val d = x.toDouble(); d != 0.0 && !d.isNaN() }
        is String -> x.isNotEmpty()
        else -> true
    }

    fun orS(a: String, b: String): String = if (a.isEmpty()) b else a
    fun orS(a: Double, b: String): String = if (a == 0.0 || a.isNaN()) b else s(a)

    fun orB(a: Boolean, b: Boolean): Boolean = a || b

    fun toFixed(x: Number, d: Int): String {
        val xd = x.toDouble()
        if (!xd.isFinite()) return "—"
        return "%.${d}f".format(Locale.US, xd)
    }

    fun numFixed(x: Number, d: Int): Double = toFixed(x, d).toDoubleOrNull() ?: 0.0

    private fun nf(minF: Int, maxF: Int): NumberFormat =
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = minF
            maximumFractionDigits = maxF
            isGroupingUsed = true
        }

    fun loc(x: Number, minF: Int, maxF: Int): String {
        val xd = x.toDouble()
        if (!xd.isFinite()) return "—"
        return nf(minF, maxF).format(xd)
    }

    fun locDf(x: Number): String = loc(x, 0, 3)

    /** Output stringify: mirrors JS '' + x; strips HTML tags (innerHTML outputs). */
    fun s(x: Any?): String {
        val t = when (x) {
            null -> ""
            is Double -> when {
                x.isNaN() || !x.isFinite() -> "—"
                x % 1.0 == 0.0 && abs(x) < 1e15 -> x.toLong().toString()
                else -> x.toString()
            }
            is Float -> s(x.toDouble())
            else -> x.toString()
        }
        return t.replace(Regex("<[^>]+>"), "")
    }
}

/** JS-style number + string concatenation (Kotlin has no Double.plus(String)). */
operator fun Double.plus(s: String): String = J.s(this) + s
operator fun Int.plus(s: String): String = J.s(this) + s
operator fun Long.plus(s: String): String = J.s(this) + s

package com.zaviyanllc.calcworker.data

import com.zaviyanllc.calcworker.data.gen.OmniUnitsData
import kotlin.math.*

/** OmniCalc Ultra's native core: expression evaluation + unit conversion.
 *  v1 scope: calculator + converter. Graphing ships in v2. */
object OmniCalcEngine {

    // ---------------- expression evaluator ----------------

    private val FUNCTIONS: Map<String, (Double) -> Double> = mapOf(
        "sqrt" to ::sqrt, "cbrt" to ::cbrt, "abs" to ::abs,
        "sin" to ::sin, "cos" to ::cos, "tan" to ::tan,
        "asin" to ::asin, "acos" to ::acos, "atan" to ::atan,
        "sinh" to ::sinh, "cosh" to ::cosh, "tanh" to ::tanh,
        "ln" to ::ln, "log" to ::log10, "log2" to ::log2,
        "exp" to ::exp, "floor" to ::floor, "ceil" to ::ceil,
        "round" to { round(it) }, "sign" to ::sign
    )
    private val CONSTANTS = mapOf(
        "pi" to PI, "e" to E, "tau" to 2 * PI, "phi" to 1.618033988749895
    )

    fun evaluate(raw: String): Double {
        val expr = raw.replace("×", "*").replace("÷", "/").replace(",", "").trim()
        if (expr.isEmpty()) throw IllegalArgumentException("empty")
        return Parser(expr).parse()
    }

    private class Parser(val s: String) {
        var pos = 0
        fun parse(): Double {
            val v = add()
            skipWs()
            if (pos != s.length) throw IllegalArgumentException("unexpected '${s[pos]}'")
            return v
        }
        private fun skipWs() { while (pos < s.length && s[pos].isWhitespace()) pos++ }
        private fun add(): Double {
            var v = mul(); skipWs()
            while (pos < s.length && (s[pos] == '+' || s[pos] == '-')) {
                val op = s[pos++]; val r = mul(); skipWs()
                v = if (op == '+') v + r else v - r
            }
            return v
        }
        private fun mul(): Double {
            var v = pow_(); skipWs()
            while (pos < s.length && (s[pos] == '*' || s[pos] == '/' || s[pos] == '%')) {
                val op = s[pos++]; val r = pow_(); skipWs()
                v = when (op) { '*' -> v * r; '/' -> v / r; else -> v % r }
            }
            return v
        }
        private fun pow_(): Double {
            var v = unary(); skipWs()
            if (pos < s.length && s[pos] == '^') { pos++; v = v.pow(unary()) }
            return v
        }
        private fun unary(): Double {
            skipWs()
            if (pos < s.length && s[pos] == '-') { pos++; return -unary() }
            if (pos < s.length && s[pos] == '+') { pos++; return unary() }
            return primary()
        }
        private fun primary(): Double {
            skipWs()
            if (pos < s.length && s[pos] == '(') {
                pos++; val v = add(); skipWs()
                if (pos >= s.length || s[pos] != ')') throw IllegalArgumentException("missing )")
                pos++; return v
            }
            if (pos < s.length && s[pos].isLetter()) {
                val start = pos
                while (pos < s.length && (s[pos].isLetterOrDigit() || s[pos] == '_')) pos++
                val name = s.substring(start, pos).lowercase()
                skipWs()
                if (pos < s.length && s[pos] == '(') {
                    pos++; val arg = add(); skipWs()
                    if (pos >= s.length || s[pos] != ')') throw IllegalArgumentException("missing )")
                    pos++
                    return FUNCTIONS[name]?.invoke(arg)
                        ?: throw IllegalArgumentException("unknown function $name")
                }
                return CONSTANTS[name] ?: throw IllegalArgumentException("unknown '$name'")
            }
            val start = pos
            while (pos < s.length && (s[pos].isDigit() || s[pos] == '.')) pos++
            if (start == pos) throw IllegalArgumentException("expected number")
            return s.substring(start, pos).toDouble()
        }
    }

    fun formatResult(x: Double): String {
        if (x.isNaN()) return "Error"
        if (x.isInfinite()) return if (x > 0) "∞" else "-∞"
        val ax = abs(x)
        if (ax != 0.0 && (ax >= 1e12 || ax < 1e-9)) return "%.6e".format(x)
        val rounded = round(x * 1e10) / 1e10
        return if (rounded % 1.0 == 0.0 && abs(rounded) < 1e15) rounded.toLong().toString()
        else rounded.toString()
    }

    // ---------------- unit conversion ----------------

    val categories = OmniUnitsData.categories.filter { it.key != "currency" }

    fun convert(value: Double, categoryKey: String, fromCode: String, toCode: String): Double {
        val cat = categories.firstOrNull { it.key == categoryKey } ?: return value
        if (cat.specialTemp) return convertTemp(value, fromCode, toCode)
        val from = cat.units.firstOrNull { it.code == fromCode } ?: return value
        val to = cat.units.firstOrNull { it.code == toCode } ?: return value
        if (to.factor == 0.0) return 0.0
        return value * from.factor / to.factor
    }

    private fun convertTemp(v: Double, from: String, to: String): Double {
        // normalize to Celsius first
        val c = when (from.lowercase()) {
            "c", "celsius" -> v
            "f", "fahrenheit" -> (v - 32) * 5 / 9
            "k", "kelvin" -> v - 273.15
            else -> v
        }
        return when (to.lowercase()) {
            "c", "celsius" -> c
            "f", "fahrenheit" -> c * 9 / 5 + 32
            "k", "kelvin" -> c + 273.15
            else -> c
        }
    }
}

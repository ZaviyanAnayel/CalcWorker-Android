package com.zaviyanllc.calcworker.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.pow

/** CalcWorker AI: server-backed chat with a fully offline fallback.
 *  The Groq API key lives only in the Vercel server env — never in the APK.
 *  Identity everywhere: "CalcWorker AI by Zaviyan". */
class AiRepository(
    /** Fast connectivity probe — when false we answer from the offline KB instantly,
     *  without waiting for HTTP timeouts. */
    private val isOnline: () -> Boolean = { true }
) {

    data class Msg(val role: String, val content: String)

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    sealed interface Answer {
        data class Online(val text: String) : Answer
        data class Offline(val text: String) : Answer
    }

    /** Ask the AI. Falls back to the on-device helper when offline or the service fails. */
    suspend fun ask(prompt: String, history: List<Msg>): Answer = withContext(Dispatchers.IO) {
        // No connection: answer instantly from the bundled offline knowledge base.
        if (!isOnline()) return@withContext Answer.Offline(offlineAnswer(prompt))
        val enriched = enrich(prompt)
        try {
            val body = JSONObject()
                .put("prompt", enriched)
                .put("history", JSONArray(history.takeLast(6).map {
                    JSONObject().put("role", it.role).put("content", it.content)
                }))
                .toString()
            val req = Request.Builder()
                .url("https://calcworker.com/api/ai")
                .header("Content-Type", "application/json")
                .header("X-CalcWorker-App", "android")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()
            client.newCall(req).execute().use { resp ->
                val raw = resp.body?.string() ?: ""
                if (!resp.isSuccessful) return@withContext Answer.Offline(offlineAnswer(prompt))
                val reply = try {
                    JSONObject(raw).optString("reply", "")
                } catch (_: Exception) { "" }
                if (reply.isBlank()) Answer.Offline(offlineAnswer(prompt))
                else Answer.Online(reply)
            }
        } catch (_: Exception) {
            Answer.Offline(offlineAnswer(prompt))
        }
    }

    /** Inject the top matching calculators' formulas so the model answers precisely.
     *  Server caps prompts at 600 chars — keep the user text, append compact context. */
    private fun enrich(prompt: String): String {
        val matches = CalcRegistry.matchCalculators(prompt, 3)
        if (matches.isEmpty()) return prompt.take(600)
        val ctx = matches.joinToString("\n") { e ->
            "- ${e.title}: ${e.formula.take(140)}"
        }
        return (prompt.take(380) + "\n[Relevant calculators]\n" + ctx).take(600)
    }

    // ---------------- fully offline helper ----------------

    fun offlineAnswer(prompt: String): String {
        val q = prompt.lowercase().trim()
        if (q.isEmpty()) return "Ask me anything — math, formulas, or which calculator to use."
        if (isGreeting(q)) return greeting()
        if (isFounder(q)) return "CalcWorker was founded and is run by Zaviyan."
        if (isIdentity(q)) return "I'm CalcWorker AI by Zaviyan — your calculation assistant inside the CalcWorker app."
        tryMath(q)?.let { return it }
        // step-by-step formula explanations for "explain X" / "how is X calculated" questions
        if (isExplainIntent(q)) {
            FormulaGuides.match(prompt)?.let { g -> return FormulaGuides.render(g) }
        }
        val matches = CalcRegistry.matchCalculators(prompt, 3)
            .filter { qualityMatch(q, it.title, it.keywords) }
        if (matches.isNotEmpty()) {
            val sb = StringBuilder("Here's what I found:\n\n")
            matches.forEach { e ->
                sb.append("• ${e.title}\n")
                if (e.formula.isNotBlank()) sb.append("  Formula: ${e.formula.take(220)}\n")
            }
            sb.append("\nOpen it from the home screen to calculate instantly — no internet needed.")
            return sb.toString()
        }
        return "I need the internet for that one, and you're offline right now. " +
            "Try the search bar above — all 100+ calculators work offline."
    }

    /** Guard against low-quality keyword matches: the question must share a
     *  meaningful word (4+ chars) with the calculator's title or keywords. */
    private fun qualityMatch(q: String, title: String, keywords: List<String>): Boolean {
        val qWords = q.split(Regex("[^a-z0-9]+")).filter { it.length >= 4 }.toSet()
        if (qWords.isEmpty()) return false
        val hay = (title.lowercase() + " " + keywords.joinToString(" ").lowercase())
        return qWords.any { hay.contains(it) }
    }

    private fun isGreeting(q: String): Boolean {
        val g = listOf("hello", "hi", "hey", "salam", "aoa", "assalam", "good morning", "good evening", "kese ho", "kaise ho")
        return g.any { q == it || q.startsWith("$it ") || q.startsWith("$it!") }
    }

    private fun greeting(): String =
        "Hello! I'm CalcWorker AI by Zaviyan.\n\n" +
        "• Do math instantly — try \"18% of 450\" or \"2500 * 12\"\n" +
        "• Find any calculator — try \"mortgage payment\" or \"bmi\"\n" +
        "• Ask how a formula works — try \"how is tip calculated\"\n\n" +
        "What would you like to compute?"

    private fun isFounder(q: String): Boolean =
        listOf("founder", "founded", "created", "made this", "built this", "owner", "who runs", "developer", "who made", "who built").any { q.contains(it) }

    private fun isIdentity(q: String): Boolean =
        listOf("who are you", "your name", "what are you", "what model").any { q.contains(it) }

    /** "explain the mortgage formula", "how is bmi calculated", "how does compound interest work" */
    private fun isExplainIntent(q: String): Boolean =
        listOf(
            "explain", "how is", "how are", "how does", "how do",
            "formula", "calculate", "calculation", "steps",
            "how it works", "work out", "derive", "breakdown"
        ).any { q.contains(it) }

    /** Tiny expression evaluator for the offline path: "20% of 1500", "2500*12", "sqrt(144)". */
    private fun tryMath(q: String): String? {
        var expr = q.replace("×", "*").replace("÷", "/").replace(",", "")
        val pctOf = Regex("""([\d.]+)\s*%\s*of\s*([\d.]+)""").find(expr)
        if (pctOf != null) {
            val p = pctOf.groupValues[1].toDoubleOrNull() ?: return null
            val v = pctOf.groupValues[2].toDoubleOrNull() ?: return null
            return "${fmt(p)}% of ${fmt(v)} = ${fmt(p / 100 * v)}"
        }
        if (!expr.matches(Regex("""[\d.\s+\-*/^%()]*""")) || expr.isBlank()) return null
        if (!expr.any { it.isDigit() }) return null
        return try {
            val value = evalExpr(expr)
            if (value.isNaN() || value.isInfinite()) null
            else "$expr = ${fmt(value)}"
        } catch (_: Exception) { null }
    }

    private fun fmt(x: Double): String =
        if (x % 1.0 == 0.0 && kotlin.math.abs(x) < 1e15) x.toLong().toString() else "%.4f".format(x).trimEnd('0').trimEnd('.')

    // recursive-descent: + - * / % ^ and parens
    private var pos = 0
    private lateinit var s: String
    private fun evalExpr(e: String): Double {
        s = e.replace(" ", ""); pos = 0
        val v = parseAdd()
        if (pos != s.length) throw IllegalArgumentException("bad expr")
        return v
    }
    private fun parseAdd(): Double {
        var v = parseMul()
        while (pos < s.length && (s[pos] == '+' || s[pos] == '-')) {
            val op = s[pos++]
            val r = parseMul()
            v = if (op == '+') v + r else v - r
        }
        return v
    }
    private fun parseMul(): Double {
        var v = parsePow()
        while (pos < s.length && (s[pos] == '*' || s[pos] == '/' || s[pos] == '%')) {
            val op = s[pos++]
            val r = parsePow()
            v = when (op) { '*' -> v * r; '/' -> v / r; else -> v % r }
        }
        return v
    }
    private fun parsePow(): Double {
        var v = parseUnary()
        if (pos < s.length && s[pos] == '^') { pos++; v = v.pow(parseUnary()) }
        return v
    }
    private fun parseUnary(): Double {
        if (pos < s.length && s[pos] == '-') { pos++; return -parseUnary() }
        if (pos < s.length && s[pos] == '+') { pos++; return parseUnary() }
        return parsePrimary()
    }
    private fun parsePrimary(): Double {
        if (pos < s.length && s[pos] == '(') {
            pos++; val v = parseAdd()
            if (pos >= s.length || s[pos] != ')') throw IllegalArgumentException(")")
            pos++; return v
        }
        val start = pos
        while (pos < s.length && (s[pos].isDigit() || s[pos] == '.')) pos++
        if (start == pos) throw IllegalArgumentException("num")
        return s.substring(start, pos).toDouble()
    }
}

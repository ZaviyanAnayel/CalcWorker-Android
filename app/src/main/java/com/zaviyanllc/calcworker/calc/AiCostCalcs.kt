package com.zaviyanllc.calcworker.calc

import kotlin.math.max
import kotlin.math.round

/** Hand-written AI-cost calculators (pure pricing math, no API calls).
 *  Pricing: official 2026 per-1M-token USD rates, mirroring the website. */

/** Official 2026 LLM pricing: id -> (name, input $/1M, output $/1M, cached-input $/1M). */
private val LLM_MODELS = mapOf(
    "gpt-4o" to Triple("GPT-4o", 2.50, 10.00),
    "gpt-4o-mini" to Triple("GPT-4o-mini", 0.15, 0.60),
    "o1" to Triple("o1 (Reasoning)", 15.00, 60.00),
    "o1-mini" to Triple("o1-mini", 3.00, 12.00),
    "claude-3-5-sonnet" to Triple("Claude 3.5 Sonnet", 3.00, 15.00),
    "claude-3-5-haiku" to Triple("Claude 3.5 Haiku", 0.80, 4.00),
    "claude-3-opus" to Triple("Claude 3 Opus", 15.00, 75.00),
    "gemini-1-5-pro" to Triple("Gemini 1.5 Pro", 1.25, 5.00),
    "gemini-1-5-flash" to Triple("Gemini 1.5 Flash", 0.075, 0.30),
    "gemini-2-0-flash" to Triple("Gemini 2.0 Flash", 0.10, 0.40),
    "deepseek-v3" to Triple("DeepSeek-V3", 0.14, 0.28),
    "deepseek-r1" to Triple("DeepSeek-R1 (Reasoning)", 0.55, 2.19),
    "llama-3-3-70b" to Triple("Llama 3.3 70B", 0.60, 0.80)
)
private val LLM_CACHED = mapOf(
    "gpt-4o" to 1.25, "gpt-4o-mini" to 0.075, "o1" to 7.50, "o1-mini" to 1.50,
    "claude-3-5-sonnet" to 0.30, "claude-3-5-haiku" to 0.08, "claude-3-opus" to 1.50,
    "gemini-1-5-pro" to 0.31, "gemini-1-5-flash" to 0.018, "gemini-2-0-flash" to 0.025,
    "deepseek-v3" to 0.014, "deepseek-r1" to 0.14, "llama-3-3-70b" to 0.60
)

private fun usd(x: Double): String = "$" + J.loc(x, 2, 2)
private fun usd4(x: Double): String = "$" + J.loc(x, 4, 4)

fun calc_ai_token_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val chars = inp.num("chars")
    val window = inp.num("window").let { if (it == 0.0) 128000.0 else it }
    val price = inp.num("price")
    val tokens = chars / 4.0
    val words = chars / 5.0
    val pages = tokens / 500.0
    val cost = tokens / 1_000_000 * price
    out["resTok"] = J.loc(tokens, 0, 0) + " tokens"
    out["resWords"] = J.loc(words, 0, 0) + " words"
    out["resWin"] = J.toFixed(tokens / window * 100, 2) + "% of context"
    out["resCost"] = usd4(cost)
    out["resPages"] = J.toFixed(pages, 1) + " pages"
    out["resScale"] = if (tokens >= 1_000_000) J.toFixed(tokens / 1_000_000, 2) + "M tokens"
        else J.loc(tokens / 1000, 1, 1) + "K tokens"
    return out
}

fun calc_ai_prompt_cost_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val sysChars = inp.str("systemPromptInput").length.toDouble()
    val userChars = inp.str("userPromptInput").length.toDouble()
    val outTokens = inp.num("outputTokensRange")
    val dailyReq = inp.num("dailyRequestsInput")
    val caching = inp.bool("promptCachingToggle")
    val batch = inp.bool("batchApiToggle")
    val modelId = inp.str("primaryModelSelect").ifEmpty { "gpt-4o" }
    val (name, inRate, outRate) = LLM_MODELS[modelId] ?: LLM_MODELS["gpt-4o"]!!
    val cachedRate = LLM_CACHED[modelId] ?: inRate
    val sysTokens = sysChars / 4.0
    val userTokens = userChars / 4.0
    val inTokens = sysTokens + userTokens
    // cached input (system prompt) billed at cached rate when toggle on
    val inCost = (userTokens * inRate + (if (caching) sysTokens * cachedRate else sysTokens * inRate)) / 1_000_000
    val outCost = outTokens * outRate / 1_000_000
    var single = inCost + outCost
    if (batch) single *= 0.5
    val monthly = single * dailyReq * 30
    val annual = monthly * 12
    val noCacheSingle = ((sysTokens + userTokens) * inRate / 1_000_000 + outCost) * (if (batch) 0.5 else 1.0)
    out["outputTokensDisplay"] = J.loc(outTokens, 0, 0) + " tokens"
    out["kpiTotalInputTokens"] = J.loc(inTokens, 0, 0)
    out["kpiInputCharWord"] = J.loc(sysChars + userChars, 0, 0) + " chars"
    out["kpiSingleCallCost"] = usd4(single)
    out["kpiPrimaryModelName"] = name
    out["kpiMonthlyCost"] = usd(monthly)
    out["kpiDailyReqSub"] = J.loc(dailyReq, 0, 0) + " req/day"
    out["kpiPerThousandCost"] = usd(single * 1000)
    out["kpiAnnualCost"] = usd(annual)
    out["kpiCachingSavings"] = usd(max(0.0, noCacheSingle - single) * dailyReq * 30) + " / mo"
    return out
}

private fun parsePair(v: String): Pair<Double, Double> {
    val p = v.split("|")
    return (p.getOrNull(0)?.toDoubleOrNull() ?: 0.0) to (p.getOrNull(1)?.toDoubleOrNull() ?: 0.0)
}

fun calc_openai_api_cost_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val (inRate, outRate) = parsePair(inp.str("model"))
    val inTok = inp.num("inTok")
    val outTok = inp.num("outTok")
    val reqs = inp.num("reqs").let { if (it == 0.0) 1.0 else it }
    val inCost = inTok / 1_000_000 * inRate
    val outCost = outTok / 1_000_000 * outRate
    val perReq = inCost + outCost
    val total = perReq * reqs
    val totTok = inTok + outTok
    out["resTotal"] = usd(total)
    out["resSplit"] = usd(inCost * reqs) + " in / " + usd(outCost * reqs) + " out"
    out["resIn"] = usd(inCost * reqs)
    out["resOut"] = usd(outCost * reqs)
    out["resPerReq"] = usd4(perReq)
    out["resPerK"] = if (totTok > 0) usd4(perReq / totTok * 1000) else "—"
    out["resAnnual"] = usd(total * 12)
    return out
}

fun calc_claude_api_cost_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val (inRate, outRate) = parsePair(inp.str("model"))
    val inTok = inp.num("inTok")
    val outTok = inp.num("outTok")
    val cachePct = inp.num("cachePct") / 100
    val cachedTok = inTok * cachePct
    val freshTok = inTok - cachedTok
    // prompt caching: cached reads billed at 10% of input rate
    val inCost = (freshTok * inRate + cachedTok * inRate * 0.10) / 1_000_000
    val outCost = outTok / 1_000_000 * outRate
    val total = inCost + outCost
    val noCache = inTok / 1_000_000 * inRate + outCost
    out["resTotal"] = usd4(total)
    out["resSplit"] = usd4(inCost) + " in / " + usd4(outCost) + " out"
    out["resIn"] = usd4(inCost)
    out["resOut"] = usd4(outCost)
    out["resSave"] = usd4(max(0.0, noCache - total)) + " saved by caching"
    out["resAnnual"] = usd(total * 365)
    out["resNoCache"] = usd4(noCache) + " without cache"
    return out
}

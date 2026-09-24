package com.zaviyanllc.calcworker.calc

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Hand port of channel-growth-calculator (transpiler OVERRIDES: no generated file).
 *  Same contract as generated files: fun calc_<slug>(inp: Inp): LinkedHashMap<String, String>. */
fun calc_channel_growth_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val curr = inp.num("currSubs")
    val target = J.orD(inp.num("milestoneTarget"), 100000.0)
    val daily = J.orD(inp.num("dailyGrowth"), 1.0)

    val needed = kotlin.math.max(0.0, target - curr)
    val days = if (daily > 0) kotlin.math.ceil(needed / daily).toLong() else 0L
    val months = days / 30.4
    val pct = if (target > 0) kotlin.math.min(100.0, curr / target * 100) else 0.0
    val targetStr = LocalDate.now().plusDays(days)
        .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))

    out["resDaysNeeded"] = if (needed > 0) "$days Days" else "Milestone Reached! 🎉"
    out["resTargetDate"] = if (needed > 0) "Projected Date: $targetStr" else "Congratulations!"
    out["resSubsNeeded"] = J.loc(needed, 0, 0) + " Subs"
    out["resMonthlyRun"] = "+" + J.loc(kotlin.math.round(daily * 30.4), 0, 0) + " / mo"
    out["resMonthsVal"] = J.toFixed(months, 1) + " Months"
    out["resProgressPct"] = J.toFixed(pct, 1) + "%"
    return out
}

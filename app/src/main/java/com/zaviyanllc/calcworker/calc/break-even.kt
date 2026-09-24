package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_break_even(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

      val p = Inp(mapOf("fixedCosts" to J.dbl(inp.num("fixedCosts")), "variableCost" to J.dbl(inp.num("variableCost")), "unitPrice" to J.dbl(inp.num("unitPrice")), "targetProfit" to J.dbl(inp.num("targetProfit"))))

      val res = CalcLib.calcBreakEven(p);
      out["resBreakEvenUnits"] = J.s("${J.locDf((res.num("breakEvenUnits")))} Units");
      out["resBreakEvenRev"] = J.s("Monthly Break-Even Revenue: ${'$'}${J.locDf((res.num("breakEvenRevenue")))}");
      out["resCM"] = J.s("${'$'}${J.toFixed((res.num("contributionMargin")), (2).toInt())}");
      out["resCMRatio"] = J.s("${res.num("contributionMarginRatio")}%");
      out["resTargetUnits"] = J.s("${J.locDf((res.num("targetUnits")))} Units");
      out["resTargetRev"] = J.s("${'$'}${J.locDf((res.num("targetRevenue")))}");
      out["resDailyUnits"] = J.s("${J.toFixed(((res.num("breakEvenUnits") / 30)), (1).toInt())} Units / day");
    
    return out
}

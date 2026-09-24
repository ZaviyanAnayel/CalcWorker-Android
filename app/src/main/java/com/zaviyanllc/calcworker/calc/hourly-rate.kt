package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_hourly_rate(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

      val p = Inp(mapOf("desiredNet" to J.dbl(inp.num("desiredNet")), "expenses" to J.dbl(inp.num("expenses")), "billableHrsPerWk" to J.dbl(inp.num("billableHours")), "weeksWorked" to J.dbl(inp.num("weeksWorked")), "taxRate" to J.dbl(inp.num("taxRate")), "profitBuffer" to J.dbl(inp.num("profitBuffer"))))
      val res = CalcLib.calcHourlyRate(p);
      out["resHourly"] = J.s("${'$'}${res.num("hourlyRate")}");
      out["resSub"] = J.s("Based on ${J.locDf((res.num("totalBillableHours")))} billable hours / year");
      out["resDay"] = J.s("${'$'}${J.locDf((res.num("dayRate")))}");
      out["resWeekly"] = J.s("${'$'}${J.locDf((res.num("weeklyRate")))}");
      out["resGross"] = J.s("${'$'}${J.locDf((res.num("annualGrossTarget")))}");
      out["resMonthly"] = J.s("${'$'}${J.locDf((res.num("monthlyGross")))}");
      out["resTaxes"] = J.s("${'$'}${J.locDf((res.num("totalTaxesEstimated")))}");
    
    return out
}

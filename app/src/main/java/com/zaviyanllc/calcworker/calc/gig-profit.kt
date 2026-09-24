package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_gig_profit(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

      val p = Inp(mapOf("grossWeekly" to J.dbl(inp.num("grossWeekly")), "milesWeekly" to J.dbl(inp.num("milesWeekly")), "hoursWeekly" to J.dbl(inp.num("hoursWeekly")), "gasPrice" to J.dbl(inp.num("gasPrice")), "mpg" to J.dbl(inp.num("mpg")), "maintPerMile" to J.dbl(inp.num("maintPerMile"))))
      val res = CalcLib.calcGigProfit(p);
      out["resNetHourly"] = J.s("${'$'}${J.toFixed((res.num("netHourlyActual")), (2).toInt())}");
      out["resGrossHourly"] = J.s("Gross app rate: ${'$'}${J.toFixed((res.num("grossHourly")), (2).toInt())} / hr");
      out["resNetWeekly"] = J.s("${'$'}${J.locDf((res.num("netWeekly")))}");
      out["resAnnualNet"] = J.s("${'$'}${J.locDf((res.num("annualNet")))}");
      out["resIRSDeduction"] = J.s("${'$'}${J.locDf((res.num("irsDeductionWeekly")))} / wk");
      out["resGasCost"] = J.s("${'$'}${J.locDf((res.num("gasCostWeekly")))}");
      out["resMaintCost"] = J.s("${'$'}${J.locDf((res.num("maintCostWeekly")))}");
      out["resTaxableIncome"] = J.s("${'$'}${J.locDf((res.num("taxableIncomeIRS")))}");
    
    return out
}

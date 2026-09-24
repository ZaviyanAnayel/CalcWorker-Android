package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_retirement_401k(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

      val matchVal = inp.str("matchPct").split("_");
      val p = Inp(mapOf("currentAge" to J.dbl(J.piD(inp.str("currentAge"), 10)), "retirementAge" to J.dbl(J.piD(inp.str("retireAge"), 10)), "currentSavings" to J.dbl(inp.num("curSavings")), "salary" to J.dbl(inp.num("salary")), "contribPct" to J.dbl(inp.num("contribPct")), "matchPct" to J.dbl(J.pf(matchVal[0])), "matchUpTo" to J.dbl(J.pf(matchVal.getOrElse(1) { "0" })), "annualReturn" to J.dbl(inp.num("annualReturn"))))

      val res = CalcLib.calcRetirement(p);
      out["resNestEgg"] = J.s("${'$'}${J.locDf((res.num("totalNestEgg")))}");
      out["resSafeIncome"] = J.s("Safe 4% Monthly Budget: ${'$'}${J.locDf((res.num("safeMonthlyIncome")))} / mo");
      out["resMonthlyIncome"] = J.s("${'$'}${J.locDf((res.num("safeMonthlyIncome")))} / mo");
      out["resEmployeeTotal"] = J.s("${'$'}${J.locDf((res.num("totalEmployeeContributions")))}");
      out["resEmployerTotal"] = J.s("${'$'}${J.locDf((res.num("totalEmployerMatch")))}");
      out["resInterestEarned"] = J.s("${'$'}${J.locDf((res.num("totalInterestEarned")))}");
      out["resYears"] = J.s("${res.num("yearsToRetire")} Years");
    
    return out
}

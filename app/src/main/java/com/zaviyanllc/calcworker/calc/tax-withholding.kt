package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_tax_withholding(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

      val p = Inp(mapOf("annualGross" to J.dbl(inp.num("annualGross")), "filingStatus" to J.dbl(inp.str("filingStatus")), "state" to J.dbl(inp.str("stateSelect")), "preTaxDeductions" to J.dbl(inp.num("preTaxDeductions"))))
      val res = CalcLib.calcTaxWithholding(p);
      out["resBiWeekly"] = J.s("${'$'}${J.locDf((res.num("netBiWeekly")))}");
      out["resMonthly"] = J.s("Net Monthly: ${'$'}${J.locDf((res.num("netMonthly")))} / mo");
      out["resNetAnnual"] = J.s("${'$'}${J.locDf((res.num("netPayAnnual")))}");
      out["resEffectiveRate"] = J.s("${res.num("effectiveRate")}%");
      out["resFedTax"] = J.s("${'$'}${J.locDf((res.num("fedTax")))}");
      out["resFicaTax"] = J.s("${'$'}${J.locDf((res.num("ficaTax")))}");
      out["resStateTax"] = J.s("${'$'}${J.locDf((res.num("stateTax")))}");
      out["resTotalTaxes"] = J.s("${'$'}${J.locDf((res.num("totalTaxes")))}");
    
    return out
}

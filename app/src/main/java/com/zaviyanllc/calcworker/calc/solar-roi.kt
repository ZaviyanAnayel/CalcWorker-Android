package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_solar_roi(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

      val p = Inp(mapOf("monthlyBill" to J.dbl(inp.num("monthlyBill")), "tariffRate" to J.dbl(inp.num("tariffRate")), "systemSizeKW" to J.dbl(inp.num("systemSize")), "sunHours" to J.dbl(inp.num("sunHours"))))

      val res = CalcLib.calcSolarROI(p);
      out["resSavings25"] = J.s("${'$'}${J.locDf((res.num("net25YrSavings")))}");
      out["resPayback"] = J.s("Payback Timeline: ${res.num("paybackYears")} Years");
      out["resTaxCredit"] = J.s("${'$'}${J.locDf((res.num("federalTaxCredit")))}");
      out["resNetCost"] = J.s("${'$'}${J.locDf((res.num("netCost")))}");
      out["resAnnualKwh"] = J.s("${J.locDf((res.num("annualKwh")))} kWh");
      out["resYear1Offset"] = J.s("${'$'}${J.locDf((res.num("annualSavings")))} / yr");
      out["resROI"] = J.s("${res.num("roiPercent")}%");
    
    return out
}

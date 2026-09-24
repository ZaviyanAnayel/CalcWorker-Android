package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_cobra_insurance_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val empShare =J.orD((inp.num("employeeMonthlyPayrollDeduction")), (0));
            val erPct = (J.orD((inp.num("employerSharePct")), (75))) / 100;
            val months =J.orD((J.piD(inp.str("cobraDurationMonths"), 10)), (6));

            val totalPlanRate = empShare / (1 - erPct);
            val erSubsidy = totalPlanRate * erPct;
            val cobraMonthly = totalPlanRate * 1.02; 
            val adminFee = totalPlanRate * 0.02;
            val totalPeriod = cobraMonthly * months;

            out["resMonthlyCobraPayment"] = J.s("${'$'}" +J.locDf((J.round(cobraMonthly))) + " / mo");
            out["resTotalPeriodCost"] = J.s("${'$'}" +J.locDf((J.round(totalPeriod))));
            out["resSubsidyLost"] = J.s("-${'$'}" +J.locDf((J.round(erSubsidy))) + " / mo");
            out["resTotalGroupRate"] = J.s("${'$'}" +J.locDf((J.round(totalPlanRate))) + " / month total");
            out["resAdminFee"] = J.s("${'$'}" +J.locDf((J.round(adminFee))) + " / month (2% fee)");
            out["resWindowNotice"] = J.s("Up to 18 months standard (36 months for divorce/death)");
            out["resAcaAlt"] = J.s("ACA Marketplace silver plans typically average ${'$'}450-${'$'}650/mo with subsidies");
        
  } catch (e: Exception) {
    
  }

    return out
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_emergency_fund_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val housing =J.orD((inp.num("monthlyRentMortgage")), (0));
            val food =J.orD((inp.num("monthlyFoodGroceries")), (0));
            val util =J.orD((inp.num("monthlyUtilities")), (0));
            val health =J.orD((inp.num("monthlyHealthInsurance")), (0));
            val debt =J.orD((inp.num("monthlyDebtMinimums")), (0));
            val months =J.orD((J.piD(inp.str("targetReserveMonths"), 10)), (6));
            val current =J.orD((inp.num("currentEmergencySavings")), (0));

            val monthlyBurn = housing + food + util + health + debt;
            val targetFund = monthlyBurn * months;
            val gap = J.max(0, targetFund - current);
            val progress =(if (targetFund > 0) (current / targetFund) * 100 else 100.0);

            out["resTotalTargetFund"] = J.s("${'$'}" +J.locDf((J.round(targetFund))));
            out["resFundingGap"] = J.s((if (gap > 0) "${'$'}" +J.locDf((J.round(gap))) else "${'$'}0 (Fully Funded!)"));
            out["resFundingProgress"] = J.s(J.toFixed((progress), (1).toInt()) + "% Funded");
            out["resMonthlyBurn"] = J.s("${'$'}" +J.locDf((J.round(monthlyBurn))) + " / mo (Non-negotiables)");
            out["resThreeMonth"] = J.s("${'$'}" +J.locDf((J.round(monthlyBurn * 3))));
            out["resSixMonth"] = J.s("${'$'}" +J.locDf((J.round(monthlyBurn * 6))));
            out["resMonthlyFillPlan"] = J.s((if (gap > 0) "${'$'}" +J.locDf((J.round(gap / 12))) + " / mo for 1 year" else "Surplus: Ready to invest"));
        
  } catch (e: Exception) {
    
  }

    return out
}

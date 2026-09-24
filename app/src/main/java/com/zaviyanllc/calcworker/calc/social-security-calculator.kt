package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_social_security_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val base =J.orD((inp.num("estMonthlyFraBenefit")), (0));
            val age =J.orD((J.piD(inp.str("claimAge"), 10)), (67));
            val life =J.orD((J.piD(inp.str("lifeExpectancy"), 10)), (85));

            val multipliers = Inp(mapOf("62" to 0.70, "63" to 0.75, "64" to 0.80, "65" to 0.867, "66" to 0.933, "67" to 1.00, "68" to 1.08, "69" to 1.16, "70" to 1.24))
            val factor =J.orD(multipliers.num(J.s(age)), 1.0);
            val monthly = base * factor;
            val annual = monthly * 12;
            val years = J.max(0, life - age);
            val cumulative = annual * years;

            val fraYears = J.max(0, life - 67);
            val fraCumulative = (base * 12) * fraYears;
            val diff = cumulative - fraCumulative;

            out["resMonthlyPayment"] = J.s("${'$'}" +J.locDf((J.round(monthly))));
            out["resAnnualPayment"] = J.s("${'$'}" +J.locDf((J.round(annual))));
            out["resCumulativeLifetime"] = J.s("${'$'}" +J.locDf((J.round(cumulative))));
            out["resBaseFra"] = J.s("${'$'}" +J.locDf((J.round(base))) + " / mo");
            out["resAdjustFactor"] = J.s(J.toFixed(((factor * 100)), (1).toInt()) + "% of FRA");
            out["resBenefitYears"] = J.s(years + " years (" + (years * 12) + " checks)");
            out["resLifetimeDiff"] = J.s(((if (diff >= 0) "+${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(diff)))) + " vs claiming at 67");
        
  } catch (e: Exception) {
    
  }

    return out
}

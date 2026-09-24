package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_inflation_retirement_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val spendingToday =J.orD((inp.num("todayIncomeDesired")), (0));
            val years =J.orD((inp.num("yearsUntilRetire")), (0));
            val inflRate = (J.orD((inp.num("expectedInflationRate")), (3.0))) / 100;
            val swr = (J.orD((inp.num("safeWithdrawalRate")), (4.0))) / 100;

            val inflationMultiplier = J.pw(J.dbl(1 + inflRate), J.dbl(years));
            val futureAnnualSpend = spendingToday * inflationMultiplier;
            val futureMonthlySpend = futureAnnualSpend / 12;

            val nestEggToday =(if (swr > 0) spendingToday / swr else 0.0);
            val nestEggFuture =(if (swr > 0) futureAnnualSpend / swr else 0.0);
            val inflationPenalty = nestEggFuture - nestEggToday;

            
            val purchasingPowerLoss = (1 - (1 / inflationMultiplier)) * 100;

            out["resFutureAnnualBudget"] = J.s("${'$'}" +J.locDf((J.round(futureAnnualSpend))));
            out["resRequiredNestEgg"] = J.s("${'$'}" +J.locDf((J.round(nestEggFuture))));
            out["resPurchasingPowerLoss"] = J.s("-" +J.toFixed((purchasingPowerLoss), (1).toInt()) + "%");

            out["resInflationMultiplier"] = J.s(J.toFixed((inflationMultiplier), (2).toInt()) + "x Today's Prices");
            out["resFutureMonthlyBudget"] = J.s("${'$'}" +J.locDf((J.round(futureMonthlySpend))) + " / mo");
            out["resTodayNestEgg"] = J.s("${'$'}" +J.locDf((J.round(nestEggToday))));
            out["resInflationWealthPenalty"] = J.s("+${'$'}" +J.locDf((J.round(inflationPenalty))));out["resSummary"] = J.s("Inflation & Retirement Summary: To match today's ${'$'}${J.locDf((spendingToday))}/yr in ${years} years at ${J.toFixed(((inflRate*100)), (1).toInt())}% inflation, you will need ${'$'}${J.locDf((J.round(futureAnnualSpend)))}/year. Under the ${J.toFixed(((swr*100)), (1).toInt())}% safe withdrawal rule, your required nest egg is ${'$'}${J.locDf((J.round(nestEggFuture)))} (an inflation premium of +${'$'}${J.locDf((J.round(inflationPenalty)))})."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

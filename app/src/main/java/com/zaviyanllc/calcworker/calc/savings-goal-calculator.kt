package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_savings_goal_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val target =J.orD((inp.num("targetSavingsGoal")), (0));
            val start =J.orD((inp.num("currentStartingBalance")), (0));
            val months =J.orD((J.piD(inp.str("monthsToTarget"), 10)), (12));
            val apy = (J.orD((inp.num("annualApyYield")), (0))) / 100;
            val r = apy / 12;

            val futureStart = start * J.pw(J.dbl(1 + r), J.dbl(months));
            val remainingTarget = J.max(0, target - futureStart);

            var monthlyDeposit = 0.0;
            if (r > 0) {
                monthlyDeposit = remainingTarget * (r / (J.pw(J.dbl(1 + r), J.dbl(months)) - 1));
            } else {
                monthlyDeposit = remainingTarget / months;
            }

            val totalOutOfPocket = start + (monthlyDeposit * months);
            val totalInterest = J.max(0, target - totalOutOfPocket);
            val intShare =(if (target > 0) (totalInterest / target) * 100 else 0.0);

            out["resMonthlyDeposit"] = J.s("${'$'}" +J.locDf((J.round(monthlyDeposit))) + " / mo");
            out["resTotalInterestEarned"] = J.s("+${'$'}" +J.locDf((J.round(totalInterest))));
            out["resTotalOutOfPocket"] = J.s("${'$'}" +J.locDf((J.round(totalOutOfPocket))));
            out["resWeeklyDeposit"] = J.s("${'$'}" +J.locDf((J.round(monthlyDeposit / 4.333))) + " / week");
            out["resDailyDeposit"] = J.s("${'$'}" +J.toFixed(((monthlyDeposit / 30.4)), (2).toInt()) + " / day");
            out["resInterestShare"] = J.s(J.toFixed((intShare), (1).toInt()) + "% of goal funded by compound interest");
            out["resTimeline"] = J.s(months + " months (" +J.toFixed(((months / 12)), (1).toInt()) + " years)");
        
  } catch (e: Exception) {
    
  }

    return out
}

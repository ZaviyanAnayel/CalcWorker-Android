package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_extra_mortgage_payment_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val bal =J.orD((inp.num("currentLoanBalance")), (0));
            val r = (J.orD((inp.num("interestRateApr")), (0))) / 100 / 12;
            val years =J.orD((inp.num("remainingTermYears")), (30));
            val extraMo =J.orD((inp.num("extraMonthlyPayment")), (0));
            val extraYr =J.orD((inp.num("extraAnnualLumpSum")), (0));

            val n = years * 12;
            val standardPmt = (bal * (r * J.pw(J.dbl(1 + r), J.dbl(n)))) / (J.pw(J.dbl(1 + r), J.dbl(n)) - 1);
            val totalOldInt = (standardPmt * n) - bal;

            
            var curBal = bal;
            var totalNewInt = 0.0;
            var months = 0.0;
            val maxMonths = n;

            while (curBal > 0 && months < maxMonths) {
                months += 1;
                var intPmt = curBal * r;
                totalNewInt += intPmt;
                var prinPmt = (standardPmt - intPmt) + extraMo;
                if (months % 12 == 0.0) {
                    prinPmt += extraYr;
                }
                curBal -= prinPmt;
            }

            val intSaved = J.max(0, totalOldInt - totalNewInt);
            val monthsSaved = J.max(0, n - months);
            val yearsSaved =J.toFixed(((monthsSaved / 12)), (1).toInt());
            val newYears =J.toFixed(((months / 12)), (1).toInt());

            out["resTotalInterestSaved"] = J.s("${'$'}" +J.locDf((J.round(intSaved))));
            out["resTimeShortened"] = J.s(yearsSaved + " Years Faster");
            out["resNewPayoffYears"] = J.s(newYears + " Years Total");
            out["resStandardPmt"] = J.s("${'$'}" +J.locDf((J.round(standardPmt))) + " / mo");
            out["resOldInterest"] = J.s("${'$'}" +J.locDf((J.round(totalOldInt))));
            out["resNewInterest"] = J.s("${'$'}" +J.locDf((J.round(totalNewInt))));
            out["resPayoffDate"] = J.s(monthsSaved + " monthly payments eliminated");
        
  } catch (e: Exception) {
    
  }

    return out
}

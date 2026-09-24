package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_529_college_savings_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val curAge =J.orD((J.piD(inp.str("childCurrentAge"), 10)), (0));
            val collAge =J.orD((J.piD(inp.str("collegeStartAge"), 10)), (18));
            val init =J.orD((inp.num("initial529Deposit")), (0));
            val monthly =J.orD((inp.num("monthlyContribution")), (0));
            val r = (J.orD((inp.num("expectedAnnualReturn")), (7.0))) / 100 / 12;
            val stateTax = (J.orD((inp.num("stateTaxDeductionRate")), (0))) / 100;

            val years = J.max(1, collAge - curAge);
            val n = years * 12;

            val futureVal = (init * J.pw(J.dbl(1 + r), J.dbl(n))) + (monthly * ((J.pw(J.dbl(1 + r), J.dbl(n)) - 1) / r));
            val totalDeposits = init + (monthly * n);
            val gains = J.max(0, futureVal - totalDeposits);
            val stateSavingsAnnual = (monthly * 12) * stateTax;

            
            val estFuture4YrCost = 115000;
            val coverage = (futureVal / estFuture4YrCost) * 100;

            out["resFuture529Balance"] = J.s("${'$'}" +J.locDf((J.round(futureVal))));
            out["resTaxFreeGains"] = J.s("${'$'}" +J.locDf((J.round(gains))) + " (Tax-Free)");
            out["resAnnualStateTaxSavings"] = J.s("${'$'}" +J.locDf((J.round(stateSavingsAnnual))) + " / year");
            out["resYearsToCollege"] = J.s(years + " Years (" + (years * 12) + " months)");
            out["resTotalDeposits"] = J.s("${'$'}" +J.locDf((J.round(totalDeposits))));
            out["resTuitionCoverage"] = J.s(J.toFixed((coverage), (0).toInt()) + "% of projected 4-year public university");
            out["resRothRollover"] = J.s("Up to ${'$'}35,000 lifetime rollover to beneficiary Roth IRA permitted");
        
  } catch (e: Exception) {
    
  }

    return out
}

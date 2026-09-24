package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_student_loan_pslf_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val bal =J.orD((inp.num("totalDirectLoanBalance")), (0));
            val r = (J.orD((inp.num("weightedInterestRate")), (6.25))) / 100 / 12;
            val agi =J.orD((inp.num("annualAgiIncome")), (0));
            val fam =J.orD((J.piD(inp.str("familySizeCount"), 10)), (1));
            val made =J.orD((J.piD(inp.str("qualifyingPaymentsMade"), 10)), (0));

            
            val povertyLine = 15650 + (fam - 1) * 5400;
            val discretionaryIncome = J.max(0, agi - (povertyLine * 2.25)); 
            val idrMonthly = (discretionaryIncome * 0.10) / 12; 

            val remainingMonths = J.max(0, 120 - made);
            val totalPslfPaid = (idrMonthly * remainingMonths);

            
            val n = 120;
            val standardMonthly = (bal * (r * J.pw(J.dbl(1 + r), J.dbl(n)))) / (J.pw(J.dbl(1 + r), J.dbl(n)) - 1);
            val totalStandardPaid = standardMonthly * n;

            
            val futureBal = bal * J.pw(J.dbl(1 + (r * 12)), J.dbl(10));
            val forgiven = J.max(0, (bal + (bal * r * 120)) - totalPslfPaid);
            val netSavings = J.max(0, totalStandardPaid - totalPslfPaid);

            out["resProjectedForgiveness"] = J.s("${'$'}" +J.locDf((J.round(forgiven))));
            out["resTotalPslfPaid"] = J.s("${'$'}" +J.locDf((J.round(totalPslfPaid))));
            out["resStandardTenYearTotal"] = J.s("${'$'}" +J.locDf((J.round(totalStandardPaid))));
            out["resMonthlyIdr"] = J.s("${'$'}" +J.locDf((J.round(idrMonthly))) + " / mo (Income-Driven)");
            out["resRemainingPayments"] = J.s(remainingMonths + " payments (" +J.toFixed(((remainingMonths / 12)), (1).toInt()) + " years left)");
            out["resStandardMonthly"] = J.s("${'$'}" +J.locDf((J.round(standardMonthly))) + " / mo");
            out["resNetPslfAdvantage"] = J.s("+${'$'}" +J.locDf((J.round(netSavings))) + " saved vs standard payoff");
        
  } catch (e: Exception) {
    
  }

    return out
}

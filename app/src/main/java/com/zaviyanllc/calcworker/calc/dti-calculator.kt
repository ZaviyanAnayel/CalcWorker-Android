package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_dti_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val income =J.orD((inp.num("grossMonthlyIncome")), (1));
            val piti =J.orD((inp.num("proposedMortgagePiti")), (0));
            val autoLoan =J.orD((inp.num("autoLoanPayments")), (0));
            val student =J.orD((inp.num("studentLoanPayments")), (0));
            val cc =J.orD((inp.num("creditCardMinimums")), (0));
            val other =J.orD((inp.num("otherDebts")), (0));

            val nonHousingDebt = autoLoan + student + cc + other;
            val totalDebt = piti + nonHousingDebt;

            val frontEnd = (piti / income) * 100;
            val backEnd = (totalDebt / income) * 100;

            var status = "Excellent Approval Odds";
            var color = "#10b981";
            if (backEnd > 50) {
                status = "High Risk (Exceeds Limits)";
                color = "#ef4444";
            } else if (backEnd > 43) {
                status = "FHA / VA Exception Range";
                color = "#f59e0b";
            } else if (backEnd > 36) {
                status = "Acceptable for Conventional";
                color = "#38bdf8";
            }

            val maxDebt = income * 0.43;
            val room = J.max(0, maxDebt - totalDebt);

            out["resBackEndDti"] = J.s(J.toFixed((backEnd), (1).toInt()) + "%");
            out["resFrontEndDti"] = J.s(J.toFixed((frontEnd), (1).toInt()) + "%");
            
            out["statEl"] = J.s(status);
            

            out["resTotalDebt"] = J.s("${'$'}" +J.locDf((J.round(totalDebt))) + " / mo");
            out["resConvBench"] = J.s("≤ 36% Preferred (Up to 43-45% with strong credit)");
            out["resFhaBench"] = J.s("≤ 31% Front / 43% Back (Up to 50% with reserves)");
            out["resBorrowRoom"] = J.s("${'$'}" +J.locDf((J.round(room))) + " / mo (to 43% cap)");
        
  } catch (e: Exception) {
    
  }

    return out
}

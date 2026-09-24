package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_personal_loan_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val amt =J.orD((inp.num("loanAmountRequested")), (0));
            val apr = (J.orD((inp.num("personalLoanApr")), (0))) / 100 / 12;
            val months =J.orD((J.piD(inp.str("loanTermMonths"), 10)), (36));
            val feePct = (J.orD((inp.num("originationFeePct")), (0))) / 100;

            val origFee = amt * feePct;
            val netCash = amt - origFee;

            val pmt =(if (apr > 0) (amt * (apr * J.pw(J.dbl(1 + apr), J.dbl(months)))) / (J.pw(J.dbl(1 + apr), J.dbl(months)) - 1) else amt / months);
            val totalPaid = pmt * months;
            val totalInt = J.max(0, totalPaid - amt);

            
            var curBal = amt;
            var simInt = 0.0;
            var simMonths = 0.0;
            while (curBal > 0 && simMonths < months) {
                simMonths += 1;
                var i = curBal * apr;
                simInt += i;
                curBal -= (pmt + 50 - i);
            }
            val savedWith50 = J.max(0, totalInt - simInt);

            out["resMonthlyInstallment"] = J.s("${'$'}" +J.locDf((J.round(pmt))) + " / mo");
            out["resTotalInterestPaid"] = J.s("${'$'}" +J.locDf((J.round(totalInt))));
            out["resNetCashDisbursed"] = J.s("${'$'}" +J.locDf((J.round(netCash))));
            out["resOriginationDollar"] = J.s("-${'$'}" +J.locDf((J.round(origFee))) + " (Deducted upfront)");
            out["resTotalRepaid"] = J.s("${'$'}" +J.locDf((J.round(totalPaid))) + " over " + months + " months");
            out["resEffectiveApr"] = J.s((if (feePct > 0) "Higher than nominal APR due to upfront fee deduction" else "Equal to nominal contract APR"));
            out["resExtraSave"] = J.s("Saves ${'$'}" + J.round(savedWith50) + " and clears " + (months - simMonths) + " months earlier");
        
  } catch (e: Exception) {
    
  }

    return out
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_payday_loan_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val prin =J.orD((inp.num("loanPrincipal")), (1));
            val fee =J.orD((inp.num("flatFinanceFee")), (0));
            val days =J.orD((J.piD(inp.str("loanDurationDays"), 10)), (14));
            val rollovers =J.orD((J.piD(inp.str("numRollovers"), 10)), (0));

            val trueApr = (fee / prin) * (365 / days) * 100;
            val totalCycles = rollovers + 1;
            val totalFees = fee * totalCycles;
            val totalPayback = prin + totalFees;
            val feeRatio = (totalFees / prin) * 100;

            out["resTrueApr"] = J.s(J.locDf((J.round(trueApr))) + "% APR");
            out["resTotalFeesPaid"] = J.s("${'$'}" +J.locDf((J.round(totalFees))));
            out["resTotalPayback"] = J.s("${'$'}" +J.locDf((J.round(totalPayback))));
            out["resFlatFeePct"] = J.s(J.toFixed((((fee / prin) * 100)), (1).toInt()) + "% flat fee for " + days + " days");
            out["resTotalCycles"] = J.s(totalCycles + " terms (" + (totalCycles * days) + " total days)");
            out["resFeeRatio"] = J.s(J.toFixed((feeRatio), (1).toInt()) + "% of borrowed cash");
            out["resSaferAlternatives"] = J.s("Credit union PALs (max 28% APR), payroll advance, or 0% balance transfer");
        
  } catch (e: Exception) {
    
  }

    return out
}

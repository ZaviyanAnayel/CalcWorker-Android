package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_invoice_factoring_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val inv =J.orD((inp.num("totalInvoiceValue")), (0));
            val advPct = (J.orD((inp.num("factoringAdvancePct")), (85))) / 100;
            val feePct = (J.orD((inp.num("factoringFeePerMonthPct")), (2.5))) / 100;
            val days =J.orD((J.piD(inp.str("customerDaysToPay"), 10)), (30));

            val immediateAdvance = inv * advPct;
            val reserveHeld = inv - immediateAdvance;

            
            val factorFee = inv * feePct * (days / 30);
            val finalRebate = J.max(0, reserveHeld - factorFee);
            val annualizedApr = (factorFee / immediateAdvance) * (365 / days) * 100;

            
            val twoTenApr = (2 / 98) * (365 / 20) * 100;

            out["resImmediateAdvance"] = J.s("${'$'}" +J.locDf((J.round(immediateAdvance))));
            out["resFactoringFeeCost"] = J.s("-${'$'}" +J.locDf((J.round(factorFee))));
            out["resAnnualizedApr"] = J.s(J.toFixed((annualizedApr), (1).toInt()) + "% APR");
            out["resReserveHeld"] = J.s("${'$'}" +J.locDf((J.round(reserveHeld))) + " (Held until customer pays)");
            out["resFinalRebate"] = J.s("${'$'}" +J.locDf((J.round(finalRebate))) + " (Net rebate disbursed)");
            out["resTwoTenApr"] = J.s(J.toFixed((twoTenApr), (1).toInt()) + "% APR (Foregoing 2% discount to pay in 30 days)");
            out["resVelocity"] = J.s("Unlocks cash " + days + " days faster to fund inventory and payroll");
        
  } catch (e: Exception) {
    
  }

    return out
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_child_tax_credit_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val kids =J.orD((J.piD(inp.str("numChildren"), 10)), (0));
            val status = inp.str("filingStatus");
            val earned =J.orD((inp.num("earnedIncome")), (0));
            val agi =J.orD((inp.num("agiIncome")), (0));

            val maxCredit = kids * 2000;
            val threshold =(if (status == "married") 400000 else 200000);

            var phaseCut = 0.0;
            if (agi > threshold) {
                val excess = agi - threshold;
                phaseCut = kotlin.math.ceil(excess / 1000) * 50;
            }

            val ctc = J.max(0, maxCredit - phaseCut);
            
            val earnedOver = J.max(0, earned - 2500);
            val refundableCap = kids * 1700;
            val refundable = J.min(ctc, J.min(refundableCap, earnedOver * 0.15));

            
            var eitc = 0.0;
            if (status == "married" && agi < 65000) {
                if (kids == 1.0 && agi < 48000) eitc = J.min(4200, earned * 0.34);
                else if (kids == 2.0 && agi < 55000) eitc = J.min(6900, earned * 0.40);
                else if (kids >= 3 && agi < 60000) eitc = J.min(7800, earned * 0.45);
            } else if (status != "married" && agi < 55000) {
                if (kids == 1.0 && agi < 42000) eitc = J.min(4200, earned * 0.34);
                else if (kids == 2.0 && agi < 48000) eitc = J.min(6900, earned * 0.40);
                else if (kids >= 3 && agi < 53000) eitc = J.min(7800, earned * 0.45);
            }

            out["resTotalCredit"] = J.s("${'$'}" +J.locDf((J.round(ctc))));
            out["resRefundablePart"] = J.s("${'$'}" +J.locDf((J.round(refundable))));
            out["resEstEitc"] = J.s("${'$'}" +J.locDf((J.round(eitc))));
            out["resMaxCredit"] = J.s("${'$'}" +J.locDf((J.round(maxCredit))));
            out["resPhaseThreshold"] = J.s("${'$'}" +J.locDf((threshold)) + " MAGI");
            out["resPhaseCut"] = J.s((if (phaseCut > 0) "-${'$'}" +J.locDf((phaseCut)) else "${'$'}0 (Fully Qualified)"));
            out["resNetTaxShield"] = J.s("${'$'}" +J.locDf((J.round(ctc + eitc))));
        
  } catch (e: Exception) {
    
  }

    return out
}

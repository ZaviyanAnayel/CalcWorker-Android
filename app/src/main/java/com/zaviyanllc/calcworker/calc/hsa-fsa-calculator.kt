package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_hsa_fsa_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val contrib =J.orD((inp.num("annualContribution")), (0));
            val bracket = (J.orD((inp.num("marginalTaxBracket")), (0))) / 100;
            val spend =J.orD((inp.num("annualMedicalSpend")), (0));
            val years =J.orD((J.piD(inp.str("investmentHorizon"), 10)), (1));
            val r = (J.orD((inp.num("expectedReturnRate")), (0))) / 100;

            val incomeTaxSaved = contrib * bracket;
            val ficaSaved = contrib * 0.0765; 
            val totalAnnualSaved = incomeTaxSaved + ficaSaved;

            val netInvestedAnnual = J.max(0, contrib - spend);
            var futureHsa = 0.0;
            var i: Int = 0; while (i < years) {
                futureHsa = (futureHsa + netInvestedAnnual) * (1 + r); i += 1}
            val totalContribs = netInvestedAnnual * years;
            val gains = J.max(0, futureHsa - totalContribs);

            val fsaUnspent = J.max(0, contrib - spend);
            val fsaLost = J.max(0, fsaUnspent - 640); 

            out["resInstantTaxSaved"] = J.s("${'$'}" +J.locDf((J.round(totalAnnualSaved))));
            out["resHsaFutureBalance"] = J.s("${'$'}" +J.locDf((J.round(futureHsa))));
            out["resFsaLostFunds"] = J.s((if (fsaLost > 0) "${'$'}" +J.locDf((J.round(fsaLost))) + " / yr" else "${'$'}0 (Under Rollover Limit)"));
            out["resFicaSavings"] = J.s("+${'$'}" +J.locDf((J.round(ficaSaved))) + " / year");
            out["resTotalTaxCut"] = J.s("${'$'}" +J.locDf((J.round(totalAnnualSaved))) + " / year");
            out["resTotalGains"] = J.s("${'$'}" +J.locDf((J.round(gains))) + " (100% Tax-Free)");
            out["resRolloverStatus"] = J.s("HSA: 100% Never Expires | FSA: Use-It-or-Lose-It");
        
  } catch (e: Exception) {
    
  }

    return out
}

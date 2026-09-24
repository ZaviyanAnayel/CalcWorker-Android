package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_estate_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val gross =J.orD((inp.num("grossEstateValue")), (0));
            val gifts =J.orD((inp.num("priorLifetimeGifts")), (0));
            val status = inp.str("filingStatus");
            val charity =J.orD((inp.num("charitableDeductions")), (0));

            
            val singleExemption = 13610000;
            val totalExemption =(if (status == "married") singleExemption * 2 else singleExemption);
            val availableExemption = J.max(0, totalExemption - gifts);

            val netEstate = J.max(0, gross - charity);
            val taxableSurplus = J.max(0, netEstate - availableExemption);
            val taxDue = taxableSurplus * 0.40;
            val toHeirs = gross - taxDue;
            val effRate =(if (gross > 0) (taxDue / gross) * 100 else 0.0);

            out["resTaxableEstate"] = J.s("${'$'}" +J.locDf((J.round(taxableSurplus))));
            out["resEstateTaxDue"] = J.s("${'$'}" +J.locDf((J.round(taxDue))));
            out["resNetToHeirs"] = J.s("${'$'}" +J.locDf((J.round(toHeirs))));
            out["resExemptionLimit"] = J.s("${'$'}" +J.locDf((totalExemption)) + " (Unified)");
            out["resRemainingExempt"] = J.s("${'$'}" +J.locDf((J.round(availableExemption))));
            out["resTopBracket"] = J.s("40% Flat Top Marginal Rate");
            out["resEffectiveRate"] = J.s(J.toFixed((effRate), (1).toInt()) + "% of Gross Estate");
        
  } catch (e: Exception) {
    
  }

    return out
}

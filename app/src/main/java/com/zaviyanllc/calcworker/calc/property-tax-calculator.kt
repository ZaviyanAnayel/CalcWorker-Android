package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_property_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val assessed =J.orD((inp.num("assessedHomeValue")), (0));
            val millRate =J.orD((inp.num("millageRate")), (0));
            val exempt =J.orD((inp.num("homesteadExemption")), (0));
            val ratio = (J.orD((inp.num("assessmentRatio")), (100))) / 100;

            val adjustedAssessed = assessed * ratio;
            val taxable = J.max(0, adjustedAssessed - exempt);
            val annual = (taxable * millRate) / 1000;
            val monthly = annual / 12;
            val effRate =(if (assessed > 0) (annual / assessed) * 100 else 0.0);
            val exemptSave = (exempt * millRate) / 1000;

            out["resAnnualTax"] = J.s("${'$'}" +J.locDf((J.round(annual))));
            out["resMonthlyEscrow"] = J.s("${'$'}" +J.locDf((J.round(monthly))) + " / mo");
            out["resEffectiveTaxRate"] = J.s(J.toFixed((effRate), (2).toInt()) + "% of Market Value");
            out["resTaxableVal"] = J.s("${'$'}" +J.locDf((J.round(taxable))));
            out["resExemptionSave"] = J.s("-${'$'}" +J.locDf((J.round(exemptSave))) + " / year");
            out["resPerThousand"] = J.s("${'$'}" +J.toFixed((millRate), (2).toInt()) + " per ${'$'}1,000");
            out["resQuarterly"] = J.s("${'$'}" +J.locDf((J.round(annual / 4))) + " / quarter");
        
  } catch (e: Exception) {
    
  }

    return out
}

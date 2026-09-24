package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_roth_conversion_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val amt =J.orD((inp.num("conversionAmount")), (0));
            val curRate = (J.orD((inp.num("currentTaxRate")), (0))) / 100;
            val retRate = (J.orD((inp.num("expectedRetireTaxRate")), (0))) / 100;
            val years =J.orD((inp.num("yearsToRetire")), (1));
            val r = (J.orD((inp.num("annualReturnRate")), (0))) / 100;

            val upfrontTax = amt * curRate;
            val growthFactor = J.pw(J.dbl(1 + r), J.dbl(years));

            
            val rothFuture = amt * growthFactor;

            
            val tradFuture = amt * growthFactor;
            val tradTaxInRetire = tradFuture * retRate;
            val tradAfterTax = tradFuture - tradTaxInRetire;

            
            val netGain = rothFuture - (tradAfterTax + (upfrontTax * growthFactor));

            out["resUpfrontTax"] = J.s("${'$'}" +J.locDf((J.round(upfrontTax))));
            out["resRothFutureVal"] = J.s("${'$'}" +J.locDf((J.round(rothFuture))));
            out["resNetWealthGain"] = J.s(((if (netGain >= 0) "+${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(netGain)))));
            out["resTradFutureVal"] = J.s("${'$'}" +J.locDf((J.round(tradFuture))));
            out["resRetireTaxTrad"] = J.s("-${'$'}" +J.locDf((J.round(tradTaxInRetire))) + " (" +J.toFixed(((retRate*100)), (0).toInt()) + "% rate)");
            out["resBreakEvenYears"] = J.s((if (curRate <= retRate) "Immediate (Rate is equal or lower today)" else kotlin.math.ceil(years * 0.6) + " - " + years + " Years"));
            out["resRmdBenefit"] = J.s("100% Tax-Free (Roth IRAs have zero lifetime mandatory RMDs)");
        
  } catch (e: Exception) {
    
  }

    return out
}

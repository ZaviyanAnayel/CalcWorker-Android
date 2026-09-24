package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_closing_costs_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val price =J.orD((inp.num("homePurchasePrice")), (0));
            val downPct = (J.orD((inp.num("downPaymentPercent")), (0))) / 100;
            val origPct = (J.orD((inp.num("lenderOriginationPct")), (0))) / 100;
            val taxRate = (J.orD((inp.num("propertyTaxRate")), (0))) / 100;
            val insAnnual =J.orD((inp.num("homeownersInsuranceAnnual")), (0));

            val downPayment = price * downPct;
            val loanAmt = price - downPayment;

            val lenderFees = (loanAmt * origPct) + 950; 
            val thirdParty = 650 + 450 + 125; 
            val titleEscrow = 1500 + (price * 0.005); 
            val prepaidEscrow = (insAnnual * 1.25) + ((price * taxRate) / 12 * 4) + ((loanAmt * 0.068 / 365) * 15); 

            val totalClosing = lenderFees + thirdParty + titleEscrow + prepaidEscrow;
            val cashToClose = downPayment + totalClosing;
            val pct =(if (price > 0) (totalClosing / price) * 100 else 0.0);

            out["resTotalClosingCosts"] = J.s("${'$'}" +J.locDf((J.round(totalClosing))));
            out["resCashToClose"] = J.s("${'$'}" +J.locDf((J.round(cashToClose))));
            out["resCostPercentage"] = J.s(J.toFixed((pct), (2).toInt()) + "% of Price");
            out["resLenderFees"] = J.s("${'$'}" +J.locDf((J.round(lenderFees))));
            out["resThirdPartyFees"] = J.s("${'$'}" +J.locDf((J.round(thirdParty))));
            out["resTitleEscrowFees"] = J.s("${'$'}" +J.locDf((J.round(titleEscrow))));
            out["resPrepaidEscrow"] = J.s("${'$'}" +J.locDf((J.round(prepaidEscrow))));
        
  } catch (e: Exception) {
    
  }

    return out
}

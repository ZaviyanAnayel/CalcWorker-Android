package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_job_offer_comparison_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val aBase =J.orD((inp.num("offerABase")), (0));
            val aBonus =J.orD((inp.num("offerABonus")), (0));
            val aMatchPct = (J.orD((inp.num("offerA401kMatch")), (0))) / 100;
            val aEquity =J.orD((inp.num("offerAEquity")), (0));

            val bBase =J.orD((inp.num("offerBBase")), (0));
            val bBonus =J.orD((inp.num("offerBBonus")), (0));
            val bMatchPct = (J.orD((inp.num("offerB401kMatch")), (0))) / 100;
            val bEquity =J.orD((inp.num("offerBEquity")), (0));

            val aMatchVal = aBase * aMatchPct;
            val bMatchVal = bBase * bMatchPct;

            val aTotal = aBase + aBonus + aMatchVal + aEquity;
            val bTotal = bBase + bBonus + bMatchVal + bEquity;

            val delta = kotlin.math.abs(aTotal - bTotal);
            val winner =(if (aTotal > bTotal) "Offer A Leads by +${'$'}" +J.locDf((J.round(delta))) + "/yr" else "Offer B Leads by +${'$'}" +J.locDf((J.round(delta))) + "/yr");

            out["resOfferATotal"] = J.s("${'$'}" +J.locDf((J.round(aTotal))) + " / yr");
            out["resOfferBTotal"] = J.s("${'$'}" +J.locDf((J.round(bTotal))) + " / yr");
            out["resOfferWinner"] = J.s(winner);
            out["resOfferA401kVal"] = J.s("+${'$'}" +J.locDf((J.round(aMatchVal))) + " / year (Guaranteed)");
            out["resOfferB401kVal"] = J.s("+${'$'}" +J.locDf((J.round(bMatchVal))) + " / year (Guaranteed)");
            out["resLiquidCashCompare"] = J.s("Offer A: ${'$'}" +J.locDf((J.round(aBase + aMatchVal))) + " vs Offer B: ${'$'}" +J.locDf((J.round(bBase + bMatchVal))));
            out["resStrategyRec"] = J.s((if (aTotal > bTotal) "Offer A provides higher holistic wealth despite lower base" else "Offer B maximizes guaranteed cash flow"));
        
  } catch (e: Exception) {
    
  }

    return out
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_markup_vs_margin_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val cost =J.orD((inp.num("itemCostBasis")), (0));
            val markup = (J.orD((inp.num("targetMarkupPercent")), (0))) / 100;

            val profit = cost * markup;
            val price = cost + profit;
            val margin =(if (price > 0) (profit / price) * 100 else 0.0);
            val costRatio =(if (price > 0) (cost / price) * 100 else 0.0);

            val priceFor50 = cost * 2.0;

            out["resSellingPrice"] = J.s("${'$'}" +J.toFixed((price), (2).toInt()));
            out["resGrossProfitDollar"] = J.s("${'$'}" +J.toFixed((profit), (2).toInt()));
            out["resGrossMarginPercent"] = J.s(J.toFixed((margin), (1).toInt()) + "% Margin");
            out["resCostMarkupView"] = J.s(J.toFixed(((markup * 100)), (1).toInt()) + "% Markup on Cost");
            out["resCostRatio"] = J.s(J.toFixed((costRatio), (1).toInt()) + "% of revenue");
            out["resFiftyMarginPrice"] = J.s("${'$'}" +J.toFixed((priceFor50), (2).toInt()) + " (Requires 100% markup)");
            out["resRuleOfThumb"] = J.s("Markup is always higher than Margin for any positive profit");
        
  } catch (e: Exception) {
    
  }

    return out
}

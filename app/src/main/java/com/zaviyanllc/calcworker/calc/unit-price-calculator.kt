package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_unit_price_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val qtyA =J.orD((inp.num("itemAName")), (1));
            val priceA =J.orD((inp.num("itemAPrice")), (0));
            val qtyB =J.orD((inp.num("itemBName")), (1));
            val priceB =J.orD((inp.num("itemBPrice")), (0));

            val unitCostA = priceA / ((if (qtyA > 0) qtyA else 1.0));
            val unitCostB = priceB / ((if (qtyB > 0) qtyB else 1.0));

            var winner = "Tie";
            var pctSaved = 0.0;
            var unitDiff = kotlin.math.abs(unitCostA - unitCostB);

            if (unitCostA < unitCostB) {
                winner = "Item A (Smaller Pack)";
                pctSaved = ((unitCostB - unitCostA) / unitCostB) * 100;
            } else if (unitCostB < unitCostA) {
                winner = "Item B (Bulk Pack)";
                pctSaved = ((unitCostA - unitCostB) / unitCostA) * 100;
            }

            val itemAEquivalent = unitCostA * qtyB;
            val cashSavings = J.max(0, itemAEquivalent - priceB);

            out["resBestDealWinner"] = J.s(winner);
            out["resPercentageSaved"] = J.s(J.toFixed((pctSaved), (1).toInt()) + "% Cheaper");
            out["resUnitCostDiff"] = J.s("${'$'}" +J.toFixed((unitDiff), (3).toInt()) + " / unit");

            out["resItemAUnitCost"] = J.s("${'$'}" +J.toFixed((unitCostA), (3).toInt()) + " / unit");
            out["resItemBUnitCost"] = J.s("${'$'}" +J.toFixed((unitCostB), (3).toInt()) + " / unit");
            out["resItemAEquivalent"] = J.s("${'$'}" +J.toFixed((itemAEquivalent), (2).toInt()));
            out["resTotalCashSavings"] = J.s("${'$'}" +J.toFixed((cashSavings), (2).toInt()) + " Saved");out["resSummary"] = J.s("Unit Price Result: ${winner} is ${J.toFixed((pctSaved), (1).toInt())}% cheaper! Item A is ${'$'}${J.toFixed((unitCostA), (3).toInt())}/unit vs Item B at ${'$'}${J.toFixed((unitCostB), (3).toInt())}/unit. Buying the larger size saves ${'$'}${J.toFixed((cashSavings), (2).toInt())}."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

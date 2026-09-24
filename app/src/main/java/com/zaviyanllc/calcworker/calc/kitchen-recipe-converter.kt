package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_kitchen_recipe_converter(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val origYield =J.orD((inp.num("originalServings")), (1));
            val targetYield =J.orD((inp.num("desiredServings")), (1));
            val rawQty =J.orD((inp.num("ingredientQty")), (0));
            val unit = inp.str("ingredientUnit");
            val density = inp.str("ingredientType");

            val factor = targetYield / ((if (origYield > 0) origYield else 1.0));
            val scaledQty = rawQty * factor;

            
            val densities = Inp(mapOf("water" to J.dbl(240), "flour" to J.dbl(120), "sugar" to J.dbl(200), "brownsugar" to J.dbl(220), "butter" to J.dbl(227), "honey" to J.dbl(340)))
            val gPerCup =J.orD(densities.num(density), 240.0);

            
            var cupsVal = 0.0;
            if (unit == "cups") cupsVal = scaledQty;
            else if (unit == "tbsp") cupsVal = scaledQty / 16;
            else if (unit == "tsp") cupsVal = scaledQty / 48;
            else if (unit == "floz") cupsVal = scaledQty / 8;
            else if (unit == "grams") cupsVal = scaledQty / gPerCup;
            else if (unit == "ounces") cupsVal = (scaledQty * 28.3495) / gPerCup;

            val gramsTotal = cupsVal * gPerCup;
            val ozTotal = gramsTotal / 28.3495;

            
            val totalTbsp = cupsVal * 16;
            val wholeTbsp = kotlin.math.floor(totalTbsp);
            val remainderTsp = (totalTbsp - wholeTbsp) * 3;

            out["resScaledQuantity"] = J.s(J.toFixed((scaledQty), (2).toInt()) + " " + unit);
            out["resScaledWeightGrams"] = J.s(J.round(gramsTotal) + " g");
            out["resMultiplierFactor"] = J.s(J.toFixed((factor), (2).toInt()) + "x Multiplier");

            out["resTbspBreakdown"] = J.s(wholeTbsp + " Tbsp + " +J.toFixed((remainderTsp), (1).toInt()) + " Tsp");
            out["resWeightOunces"] = J.s(J.toFixed((ozTotal), (2).toInt()) + " oz");
            out["resHalfBatch"] = J.s(J.toFixed(((rawQty * 0.5)), (2).toInt()) + " " + unit);
            out["resDoubleBatch"] = J.s(J.toFixed(((rawQty * 2.0)), (2).toInt()) + " " + unit);out["resSummary"] = J.s("Recipe Scaled (${J.toFixed((factor), (2).toInt())}x yield): ${J.toFixed((scaledQty), (2).toInt())} ${unit} (${J.round(gramsTotal)}g / ${J.toFixed((ozTotal), (2).toInt())} oz). Breakdown: ${wholeTbsp} Tbsp + ${J.toFixed((remainderTsp), (1).toInt())} Tsp."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

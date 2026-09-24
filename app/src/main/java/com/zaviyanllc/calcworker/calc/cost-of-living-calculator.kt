package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_cost_of_living_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val salary =J.orD((inp.num("currentSalary")), (0));
            val origin = inp.str("originCity");
            val target = inp.str("targetCity");

            val indices = Inp(mapOf("nyc" to J.dbl(220), "sf" to J.dbl(185), "la" to J.dbl(155), "seattle" to J.dbl(145), "chicago" to J.dbl(118), "denver" to J.dbl(115), "austin" to J.dbl(110), "dallas" to J.dbl(104), "atlanta" to J.dbl(102), "tampa" to J.dbl(101), "national" to J.dbl(100)))

            val idx1 =J.orD(indices.num(origin), 100.0);
            val idx2 =J.orD(indices.num(target), 100.0);

            val equivalent = salary * (idx2 / idx1);
            val deltaPct = ((idx2 - idx1) / idx1) * 100;
            val purchasingShift = ((salary - equivalent));

            var taxNote = "Standard State Income Tax";
            if (J.orD((target == "austin"), (target == "dallas" || target == "seattle" || target == "tampa"))) {
                taxNote = "0% State Income Tax (Keep more take-home pay)";
            } else if (target == "nyc") {
                taxNote = "High State & NYC Local Resident Tax (~10-13%)";
            } else if (J.orD((target == "sf"), (target == "la"))) {
                taxNote = "California Top Marginal Bracket (Up to 9.3% - 13.3%)";
            }

            out["resEquivalentSalary"] = J.s("${'$'}" +J.locDf((J.round(equivalent))));
            out["resColDelta"] = J.s(((if (deltaPct >= 0) "+" else "")) +J.toFixed((deltaPct), (1).toInt()) + "%");
            out["resTakeHomeDelta"] = J.s(((if (purchasingShift >= 0) "+${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(purchasingShift)))) + " purchasing gain");
            out["resOriginIndex"] = J.s(idx1 + " / 100 (US Benchmark)");
            out["resTargetIndex"] = J.s(idx2 + " / 100 (US Benchmark)");
            out["resHousingMultiple"] = J.s(J.toFixed(((idx2 / idx1)), (2).toInt()) + "x housing & rental ratio");
            out["resStateTaxNote"] = J.s(taxNote);
        
  } catch (e: Exception) {
    
  }

    return out
}

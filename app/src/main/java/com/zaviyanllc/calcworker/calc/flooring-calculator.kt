package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_flooring_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    val len =J.orD((inp.num("roomLength")), (20));
    val wid =J.orD((inp.num("roomWidth")), (15));
    val extra =J.orD((inp.num("extraSqFt")), (35));
    val wastePct =J.orD((inp.num("floorWaste")), (0.10));
    val matRate =J.orD((inp.num("matPrice")), (3.49));
    val boxSqFt =J.orD((inp.num("boxSize")), (20));
    val laborRate =J.orD((inp.num("laborPrice")), (2.00));

    val netSqFt = (len * wid) + extra;
    val wasteSqFt = netSqFt * wastePct;
    val grossSqFt = netSqFt + wasteSqFt;

    val boxesNeeded = kotlin.math.ceil(grossSqFt / (J.orD((boxSqFt), (20))));
    val purchasedSqFt = boxesNeeded * boxSqFt;

    val totalMatCost = purchasedSqFt * matRate;
    val totalLaborCost = grossSqFt * laborRate;
    val grandTotal = totalMatCost + totalLaborCost;
    val costPerSqFt = grandTotal / (J.orD((netSqFt), (1)));

    out["kpiTotalCost"] = J.s("${'$'}" +J.locDf((J.round(grandTotal))));
    out["subCostPerSqFt"] = J.s("${'$'}" +J.toFixed((costPerSqFt), (2).toInt()) + " per sq ft all-in");
    out["kpiTotalSqFt"] = J.s(J.locDf((J.round(grossSqFt))) + " Sq Ft");
    out["subNetSqFt"] = J.s("Net: " + J.round(netSqFt) + " sq ft + " +J.toFixed(((wastePct * 100)), (0).toInt()) + "% waste");
    out["kpiBoxCount"] = J.s(boxesNeeded + " Boxes");
    out["kpiMatCost"] = J.s("${'$'}" +J.locDf((J.round(totalMatCost))));
    out["subLaborCost"] = J.s("Labor: ${'$'}" +J.locDf((J.round(totalLaborCost))));

    out["rowNetSqFt"] = J.s(J.locDf((J.round(netSqFt))) + " sq ft");
    out["rowWasteSqFt"] = J.s(J.locDf((J.round(wasteSqFt))) + " sq ft");
    out["rowBoxesQty"] = J.s(boxesNeeded + " boxes (" + J.round(purchasedSqFt) + " sq ft)");
    out["rowMatRate"] = J.s("${'$'}" +J.toFixed((matRate), (2).toInt()) + "/sq ft");
    out["rowMatTotal"] = J.s("${'$'}" +J.toFixed((totalMatCost), (2).toInt()));
    out["rowLaborQty"] = J.s(J.locDf((J.round(grossSqFt))) + " sq ft");
    out["rowLaborRate"] = J.s("${'$'}" +J.toFixed((laborRate), (2).toInt()) + "/sq ft");
    out["rowLaborTotal"] = J.s("${'$'}" +J.toFixed((totalLaborCost), (2).toInt()));
    out["rowGrandTotal"] = J.s("${'$'}" +J.toFixed((grandTotal), (2).toInt()));
  } catch (e: Exception) {
    
  }

    return out
}

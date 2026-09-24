package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_nnn_lease_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val sf =J.orD((inp.num("rentableSquareFeet")), (0));
            val baseSf =J.orD((inp.num("baseRentPerSfYear")), (0));
            val taxesSf =J.orD((inp.num("propertyTaxesSf")), (0));
            val insSf =J.orD((inp.num("buildingInsuranceSf")), (0));
            val camSf =J.orD((inp.num("camChargesSf")), (0));

            val nnnSf = taxesSf + insSf + camSf;
            val totalSf = baseSf + nnnSf;

            val annualBase = sf * baseSf;
            val annualNnn = sf * nnnSf;
            val annualTotal = sf * totalSf;
            val monthlyTotal = annualTotal / 12;

            out["resTotalMonthlyRent"] = J.s("${'$'}" +J.locDf((J.round(monthlyTotal))) + " / mo");
            out["resTotalAnnualCost"] = J.s("${'$'}" +J.locDf((J.round(annualTotal))) + " / year");
            out["resTotalNnnRate"] = J.s("${'$'}" +J.toFixed((totalSf), (2).toInt()) + " / SF / yr");
            out["resBaseRentOnly"] = J.s("${'$'}" +J.locDf((J.round(annualBase / 12))) + " / mo (${'$'}" +J.locDf((J.round(annualBase))) + "/yr)");
            out["resNnnOpex"] = J.s("${'$'}" +J.locDf((J.round(annualNnn))) + " / yr (${'$'}" +J.toFixed((nnnSf), (2).toInt()) + "/SF)");
            out["resCamAnnual"] = J.s("${'$'}" +J.locDf((J.round(sf * camSf))) + " / yr (Janitorial, landscaping, repairs)");
            out["resModGrossRate"] = J.s("${'$'}" +J.toFixed((totalSf), (2).toInt()) + " / SF (If structured as full-service gross)");
        
  } catch (e: Exception) {
    
  }

    return out
}

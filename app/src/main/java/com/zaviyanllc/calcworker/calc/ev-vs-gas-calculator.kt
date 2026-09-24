package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_ev_vs_gas_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val miles =J.orD((inp.num("annualMilesDriven")), (0));
            val mpg =J.orD((inp.num("gasVehicleMpg")), (28));
            val gasPrice =J.orD((inp.num("gasPricePerGallon")), (3.55));
            val kwhPer100 =J.orD((inp.num("evEfficiencyKwh100")), (28));
            val kwhPrice =J.orD((inp.num("electricityRateKwh")), (0.16));
            val maint =J.orD((inp.num("maintSavingsAnnual")), (0));

            val gallonsUsed = miles / ((if (mpg > 0) mpg else 1.0));
            val annualGasCost = gallonsUsed * gasPrice;

            val totalKwh = (miles / 100) * kwhPer100;
            val annualEvCost = totalKwh * kwhPrice;

            val annualFuelSavings = annualGasCost - annualEvCost;
            val totalAnnualSavings = annualFuelSavings + maint;
            val fiveYearSavings = totalAnnualSavings * 5;
            val monthlySavings = totalAnnualSavings / 12;

            val gasCpm =(if (miles > 0) annualGasCost / miles else 0.0);
            val evCpm =(if (miles > 0) annualEvCost / miles else 0.0);

            out["resAnnualSavings"] = J.s("${'$'}" +J.locDf((J.round(totalAnnualSavings))));
            out["resFiveYearSavings"] = J.s("${'$'}" +J.locDf((J.round(fiveYearSavings))));
            out["resMonthlyDifference"] = J.s("${'$'}" +J.locDf((J.round(monthlySavings))));

            out["resAnnualGasCost"] = J.s("${'$'}" +J.locDf((J.round(annualGasCost))));
            out["resAnnualEvCost"] = J.s("${'$'}" +J.locDf((J.round(annualEvCost))));
            out["resGasCostPerMile"] = J.s("${'$'}" +J.toFixed((gasCpm), (3).toInt()) + " / mi");
            out["resEvCostPerMile"] = J.s("${'$'}" +J.toFixed((evCpm), (3).toInt()) + " / mi");out["resSummary"] = J.s("EV vs Gas Comparison (${J.locDf((miles))} miles/yr): Gas Costs ${'$'}${J.locDf((J.round(annualGasCost)))} (${'$'}${J.toFixed((gasCpm), (3).toInt())}/mi), EV Charging Costs ${'$'}${J.locDf((J.round(annualEvCost)))} (${'$'}${J.toFixed((evCpm), (3).toInt())}/mi). Net Annual Savings including maintenance: ${'$'}${J.locDf((J.round(totalAnnualSavings)))} (${'$'}${J.locDf((J.round(fiveYearSavings)))} over 5 years)."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

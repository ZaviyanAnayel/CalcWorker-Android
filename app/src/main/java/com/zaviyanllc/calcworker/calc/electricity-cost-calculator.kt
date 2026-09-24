package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_electricity_cost_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val watts =J.orD((inp.num("applianceWatts")), (0));
            val hours =J.orD((inp.num("hoursPerDay")), (0));
            val rate =J.orD((inp.num("kwhRateVal")), (0.17));
            val days =J.orD((inp.num("daysUsedMonth")), (30));

            val dailyKwh = (watts * hours) / 1000;
            val dailyCost = dailyKwh * rate;

            val monthlyKwh = dailyKwh * days;
            val monthlyCost = monthlyKwh * rate;

            val annualCost = monthlyCost * 12;
            val fiveYearCost = annualCost * 5;

            
            val annualKwh = monthlyKwh * 12;
            val carbonLbs = annualKwh * 0.85;

            out["resMonthlyPowerCost"] = J.s("${'$'}" +J.toFixed((monthlyCost), (2).toInt()));
            out["resAnnualPowerCost"] = J.s("${'$'}" +J.locDf((J.round(annualCost))));
            out["resDailyKwhConsumed"] = J.s(J.toFixed((dailyKwh), (2).toInt()) + " kWh / day");

            out["resDailyCostVal"] = J.s("${'$'}" +J.toFixed((dailyCost), (2).toInt()));
            out["resMonthlyKwhVal"] = J.s(J.round(monthlyKwh) + " kWh");
            out["resFiveYearCost"] = J.s("${'$'}" +J.locDf((J.round(fiveYearCost))));
            out["resCarbonFootprint"] = J.s(J.locDf((J.round(carbonLbs))) + " lbs CO2e");out["resSummary"] = J.s("Electricity Cost Summary: Running a ${watts}W appliance for ${hours} hrs/day at ${'$'}${J.toFixed((rate), (2).toInt())}/kWh costs ${'$'}${J.toFixed((monthlyCost), (2).toInt())}/month (${'$'}${J.locDf((J.round(annualCost)))}/year). Consumes ${J.toFixed((dailyKwh), (2).toInt())} kWh/day (${J.round(carbonLbs)} lbs CO2e/year)."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

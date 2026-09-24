package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_commute_cost_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val milesPerDay =J.orD((inp.num("dailyRoundTripMiles")), (0));
            val daysPerWeek =J.orD((inp.num("daysInOfficePerWeek")), (0));
            val costPerMile =J.orD((inp.num("irsStandardRate")), (0.67));
            val tollsParking =J.orD((inp.num("dailyTollsParking")), (0));
            val minsPerDay =J.orD((inp.num("roundTripMinutes")), (0));
            val hourlyWage =J.orD((inp.num("hourlyWageVal")), (0));

            val workWeeks = 48; 
            val totalWorkDays = daysPerWeek * workWeeks;

            val annualWorkMiles = milesPerDay * totalWorkDays;
            val annualVehicleCost = annualWorkMiles * costPerMile;
            val annualTollsParking = tollsParking * totalWorkDays;
            val directAnnualCost = annualVehicleCost + annualTollsParking;
            val monthlyDirectCost = directAnnualCost / 12;

            val totalHours = (minsPerDay * totalWorkDays) / 60;
            val timeValueCost = totalHours * hourlyWage;
            val totalCommuteValue = directAnnualCost + timeValueCost;

            out["resAnnualCommuteExpense"] = J.s("${'$'}" +J.locDf((J.round(directAnnualCost))));
            out["resAnnualHoursLost"] = J.s(J.locDf((J.round(totalHours))) + " hrs");
            out["resTotalValueLost"] = J.s("${'$'}" +J.locDf((J.round(totalCommuteValue))));

            out["resMonthlyDirectCost"] = J.s("${'$'}" +J.locDf((J.round(monthlyDirectCost))));
            out["resAnnualWorkMiles"] = J.s(J.locDf((J.round(annualWorkMiles))) + " mi");
            out["resTimeOpportunityCost"] = J.s("${'$'}" +J.locDf((J.round(timeValueCost))));
            out["resWfhTotalSavings"] = J.s("${'$'}" +J.locDf((J.round(directAnnualCost))) + " / yr");out["resSummary"] = J.s("Commute Cost Summary: Direct Annual Out-of-Pocket: ${'$'}${J.locDf((J.round(directAnnualCost)))} (${'$'}${J.locDf((J.round(monthlyDirectCost)))}/mo). Time Lost: ${J.round(totalHours)} hours/yr (valued at ${'$'}${J.locDf((J.round(timeValueCost)))}). Total Economic Burden: ${'$'}${J.locDf((J.round(totalCommuteValue)))}."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

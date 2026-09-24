package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_mortgage_refinance_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    val balance =J.orD((inp.num("currentBalance")), (380000));
    val currentRate = (J.orD((inp.num("currentRate")), (6.875))) / 100;
    val currentYears =J.orD((inp.num("currentYearsLeft")), (27));
    val newRate = (J.orD((inp.num("newRate")), (5.5))) / 100;
    val newYears =J.orD((inp.num("newTerm")), (30));
    val closingCosts =J.orD((inp.num("closingCosts")), (5800));
    val rollCosts =J.orB((inp.bool("rollCosts")), (false));

    val oldN = currentYears * 12;
    val oldR = currentRate / 12;
    val oldPayment = mortgage_refinance_calculator_pmt(oldR, oldN, balance);
    val oldTotalInterest = (oldPayment * oldN) - balance;
    val oldTotalCost = oldPayment * oldN;

    val newPrincipal =(if (J.truthy(rollCosts)) (balance + closingCosts) else balance);
    val newN = newYears * 12;
    val newR = newRate / 12;
    val newPayment = mortgage_refinance_calculator_pmt(newR, newN, newPrincipal);
    val newTotalInterest = (newPayment * newN) - newPrincipal;
    val newTotalCost = (newPayment * newN) + ((if (J.truthy(rollCosts)) 0.0 else closingCosts));

    val monthlySavings = oldPayment - newPayment;
    var breakEvenMonths = 0.0;
    if (monthlySavings > 0) {
      breakEvenMonths = kotlin.math.ceil(closingCosts / monthlySavings);
    }

    val fiveYearSavings = (monthlySavings * 60) - closingCosts;
    val lifetimeInterestSaved = oldTotalInterest - newTotalInterest;
    val lifetimeTotalSaved = oldTotalCost - newTotalCost;

    if (monthlySavings > 0) {
      out["kpiBreakEven"] = J.s(breakEvenMonths + " Months");
      out["subBreakEven"] = J.s(J.toFixed(((breakEvenMonths / 12)), (1).toInt()) + " Years to recover costs");
      
    } else {
      out["kpiBreakEven"] = J.s("No Break-Even");
      out["subBreakEven"] = J.s("Monthly payment increases");
      
    }

    out["kpiMonthlySavings"] = J.s(((if (monthlySavings >= 0) "${'$'}" else "-${'$'}")) +J.toFixed((kotlin.math.abs(monthlySavings)), (2).toInt()));
    out["subMonthlyOldNew"] = J.s("Old: ${'$'}" +J.locDf((J.round(oldPayment))) + " | New: ${'$'}" +J.locDf((J.round(newPayment))));
    
    out["kpi5YearSavings"] = J.s(((if (fiveYearSavings >= 0) "${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(fiveYearSavings)))));
    out["kpiLifetimeInterest"] = J.s(((if (lifetimeInterestSaved >= 0) "${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(lifetimeInterestSaved)))));

    out["tableOldPayment"] = J.s("${'$'}" +J.toFixed((oldPayment), (2).toInt()));
    out["tableNewPayment"] = J.s("${'$'}" +J.toFixed((newPayment), (2).toInt()));
    out["tableDiffPayment"] = J.s(((if (monthlySavings >= 0) "-${'$'}" else "+${'$'}")) +J.toFixed((kotlin.math.abs(monthlySavings)), (2).toInt()) + "/mo");

    out["tableOldRate"] = J.s(J.toFixed(((currentRate * 100)), (3).toInt()) + "%");
    out["tableNewRate"] = J.s(J.toFixed(((newRate * 100)), (3).toInt()) + "%");
    out["tableDiffRate"] = J.s(J.toFixed((((newRate - currentRate) * 100)), (3).toInt()) + "%");

    out["tableOldPrincipal"] = J.s("${'$'}" +J.locDf((J.round(balance))));
    out["tableNewPrincipal"] = J.s("${'$'}" +J.locDf((J.round(newPrincipal))));
    out["tableDiffPrincipal"] = J.s(((if (J.truthy(rollCosts)) "+${'$'}" +J.locDf((J.round(closingCosts))) else "${'$'}0")));

    out["tableOldTotalInterest"] = J.s("${'$'}" +J.locDf((J.round(oldTotalInterest))));
    out["tableNewTotalInterest"] = J.s("${'$'}" +J.locDf((J.round(newTotalInterest))));
    out["tableDiffTotalInterest"] = J.s(((if (lifetimeInterestSaved >= 0) "-${'$'}" else "+${'$'}")) +J.locDf((kotlin.math.abs(J.round(lifetimeInterestSaved)))));

    out["tableOldTotalCost"] = J.s("${'$'}" +J.locDf((J.round(oldTotalCost))));
    out["tableNewTotalCost"] = J.s("${'$'}" +J.locDf((J.round(newTotalCost))));
    out["tableDiffTotalCost"] = J.s(((if (lifetimeTotalSaved >= 0) "-${'$'}" else "+${'$'}")) +J.locDf((kotlin.math.abs(J.round(lifetimeTotalSaved)))));
  } catch (e: Exception) {
    
  }

    return out
}

private fun mortgage_refinance_calculator_pmt(rate: Number, nper: Number, pv: Number): Double {
    var rate = rate.toDouble();
    var nper = nper.toDouble();
    var pv = pv.toDouble();
  if (rate == 0.0) return pv / nper;
  return pv * (rate * J.pw(J.dbl(1 + rate), J.dbl(nper))) / (J.pw(J.dbl(1 + rate), J.dbl(nper)) - 1);

}

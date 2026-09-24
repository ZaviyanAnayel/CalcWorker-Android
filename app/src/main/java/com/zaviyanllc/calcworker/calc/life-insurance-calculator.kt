package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_life_insurance_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    val salary =J.orD((inp.num("annualIncome")), (95000));
    val years =J.orD((inp.num("incomeYears")), (10));
    val mortgage =J.orD((inp.num("mortgageDebt")), (285000));
    val otherDebt =J.orD((inp.num("otherDebts")), (35000));
    val education =J.orD((inp.num("educationFund")), (100000));
    val liquid =J.orD((inp.num("existingSavings")), (65000));

    val incomeComp = salary * years;
    val debtComp = otherDebt;
    val mortgageComp = mortgage;
    val educationComp = education;

    val grossNeed = debtComp + incomeComp + mortgageComp + educationComp;
    val netCoverage = J.max(0, grossNeed - liquid);

    
    val monthlyRatePerMillion = 28;
    val estTermCost = J.max(15, J.round((netCoverage / 1000000) * monthlyRatePerMillion));

    out["kpiCoverage"] = J.s("${'$'}" +J.locDf((J.round(netCoverage))));
    out["subCoverage"] = J.s(J.toFixed(((netCoverage / (J.orD((salary), (1))))), (1).toInt()) + "x Annual Gross Salary");
    out["kpiTermCost"] = J.s("${'$'}" + estTermCost + " / mo");
    out["kpiIncomeComp"] = J.s("${'$'}" +J.locDf((J.round(incomeComp))));
    out["kpiDebtClear"] = J.s("${'$'}" +J.locDf((J.round(debtComp + mortgageComp))));

    out["rowD"] = J.s("${'$'}" +J.locDf((J.round(debtComp))));
    out["rowI"] = J.s("${'$'}" +J.locDf((J.round(incomeComp))));
    out["rowM"] = J.s("${'$'}" +J.locDf((J.round(mortgageComp))));
    out["rowE"] = J.s("${'$'}" +J.locDf((J.round(educationComp))));
    out["rowLess"] = J.s("-${'$'}" +J.locDf((J.round(liquid))));
    out["rowNetPolicy"] = J.s("${'$'}" +J.locDf((J.round(netCoverage))));

    val denom =J.orD((grossNeed), (1));
    out["rowDPct"] = J.s(J.toFixed((((debtComp / denom) * 100)), (1).toInt()) + "%");
    out["rowIPct"] = J.s(J.toFixed((((incomeComp / denom) * 100)), (1).toInt()) + "%");
    out["rowMPct"] = J.s(J.toFixed((((mortgageComp / denom) * 100)), (1).toInt()) + "%");
    out["rowEPct"] = J.s(J.toFixed((((educationComp / denom) * 100)), (1).toInt()) + "%");
  } catch (e: Exception) {
    
  }

    return out
}

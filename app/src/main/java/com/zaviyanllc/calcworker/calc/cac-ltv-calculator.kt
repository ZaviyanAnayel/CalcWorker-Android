package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_cac_ltv_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val spend =J.orD((inp.num("marketingSalesSpendMonthly")), (0));
            val acquired =J.orD((J.piD(inp.str("newCustomersAcquired"), 10)), (1));
            val arpu =J.orD((inp.num("averageOrderValue")), (0));
            val margin = (J.orD((inp.num("grossMarginPct")), (70))) / 100;
            val churn = (J.orD((inp.num("monthlyChurnRate")), (5))) / 100;

            val cac = spend / acquired;
            val lifespanMonths =(if (churn > 0) 1 / churn else 24.0);
            val ltv = (arpu * margin) / (J.orD((churn), (0.01)));
            val ratio =(if (cac > 0) ltv / cac else 0.0);

            val monthlyGrossMarginUser = arpu * margin;
            val paybackMonths =(if (monthlyGrossMarginUser > 0) cac / monthlyGrossMarginUser else 0.0);

            var health = "Excellent (3:1 to 4:1 Gold Standard)";
            var color = "#10b981";
            if (ratio < 1.0) {
                health = "Unsustainable (Burning Cash on Every Customer)";
                color = "#ef4444";
            } else if (ratio < 3.0) {
                health = "Sub-Optimal (<3:1 Low Profitability)";
                color = "#f59e0b";
            } else if (ratio > 5.0) {
                health = "Under-Investing in Growth (>5:1 Opportunity to Scale)";
                color = "#38bdf8";
            }

            out["resLtvCacRatio"] = J.s(J.toFixed((ratio), (2).toInt()) + " : 1");
            out["resBlendedCac"] = J.s("${'$'}" +J.locDf((J.round(cac))));
            out["resCustomerLtv"] = J.s("${'$'}" +J.locDf((J.round(ltv))));
            out["resCacPayback"] = J.s(J.toFixed((paybackMonths), (1).toInt()) + " Months to Recover CAC");
            out["resCustomerLifespan"] = J.s(J.toFixed((lifespanMonths), (1).toInt()) + " Months (" +J.toFixed(((lifespanMonths / 12)), (1).toInt()) + " Years)");
            
            out["hEl"] = J.s(health);
            
            out["resGrowthRec"] = J.s((if (ratio >= 3.0) "Green light to scale paid advertising" else "Optimize churn and conversion rates first"));
        
  } catch (e: Exception) {
    
  }

    return out
}

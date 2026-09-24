package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_sales_commission_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val baseSalary =J.orD((inp.num("baseSalaryVal")), (0));
            val quota =J.orD((inp.num("quotaVal")), (0));
            val closedRev =J.orD((inp.num("closedRevenueVal")), (0));
            val baseRate = (J.orD((inp.num("baseCommissionRate")), (10))) / 100;
            val accelRate = (J.orD((inp.num("acceleratorRate")), (18))) / 100;

            val quotaAttainment =(if (quota > 0) (closedRev / quota) * 100 else 0.0);
            val revenueInBase = J.min(closedRev, quota);
            val baseComm = revenueInBase * baseRate;

            val revenueOverQuota = J.max(0, closedRev - quota);
            val accelComm = revenueOverQuota * accelRate;

            val totalComm = baseComm + accelComm;
            val totalComp = baseSalary + totalComm;
            val effectiveRate =(if (closedRev > 0) (totalComm / closedRev) * 100 else 0.0);

            out["resTotalCommission"] = J.s("${'$'}" +J.locDf((J.round(totalComm))));
            out["resTotalOte"] = J.s("${'$'}" +J.locDf((J.round(totalComp))));
            out["resQuotaAttainment"] = J.s(J.toFixed((quotaAttainment), (1).toInt()) + "%");

            out["resBaseTierPayout"] = J.s("${'$'}" +J.locDf((J.round(baseComm))));
            out["resAcceleratorPayout"] = J.s("${'$'}" +J.locDf((J.round(accelComm))));
            out["resEffectiveCommRate"] = J.s(J.toFixed((effectiveRate), (2).toInt()) + "%");
            out["resTakeHomeAnnual"] = J.s("${'$'}" +J.locDf((J.round(totalComp))));out["resSummary"] = J.s("Sales Commission Summary: Closed Revenue ${'$'}${J.locDf((closedRev))} (${J.toFixed((quotaAttainment), (1).toInt())}% Quota Attainment). Total Commission: ${'$'}${J.locDf((J.round(totalComm)))} (Base Tier: ${'$'}${J.locDf((J.round(baseComm)))}, Accelerated Tier: ${'$'}${J.locDf((J.round(accelComm)))}). Total Annual Earnings (Base + Comm): ${'$'}${J.locDf((J.round(totalComp)))}."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_substack_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    val freeCount =J.orD((inp.num("subFreeCount")), (12000));
    val convRate = (J.orD((inp.num("subConvRate")), (3.5))) / 100;
    val monthlyPrice =J.orD((inp.num("subMonthlyPrice")), (8));
    val annualPrice =J.orD((inp.num("subAnnualPrice")), (80));
    val annualPct =J.orD((inp.num("subAnnualPct")), (0.45));

    val paidSubscribers = J.round(freeCount * convRate);
    val annualSubs = J.round(paidSubscribers * annualPct);
    val monthlySubs = paidSubscribers - annualSubs;

    val mrrFromMonthly = monthlySubs * monthlyPrice;
    val mrrFromAnnual = (annualSubs * annualPrice) / 12;
    val totalMRR = mrrFromMonthly + mrrFromAnnual;
    val totalARR = totalMRR * 12;

    val substackFeeA = totalARR * 0.10;
    val substackFeeM = substackFeeA / 12;

    
    
    val totalChargesPerYear = (monthlySubs * 12) + annualSubs;
    val stripeFeeA = (totalARR * 0.029) + (totalChargesPerYear * 0.30);
    val stripeFeeM = stripeFeeA / 12;

    val netAnnual = J.max(0, totalARR - substackFeeA - stripeFeeA);
    val netMonthly = netAnnual / 12;
    val retentionRate =(if (totalARR > 0) (netAnnual / totalARR) * 100 else 0.0);

    out["kpiAnnualNet"] = J.s("${'$'}" +J.locDf((J.round(netAnnual))));
    out["subTakeHomePct"] = J.s(J.toFixed((retentionRate), (1).toInt()) + "% Creator Retention");
    out["kpiMonthlyNet"] = J.s("${'$'}" +J.toFixed((netMonthly), (2).toInt()));
    out["kpiPaidCount"] = J.s(J.locDf((paidSubscribers)) + " Paid");
    out["subPaidRatio"] = J.s("From " +J.locDf((J.round(freeCount))) + " free readers");
    out["kpiGrossAnnual"] = J.s("${'$'}" +J.locDf((J.round(totalARR))));

    out["rowGrossM"] = J.s("${'$'}" +J.toFixed((totalMRR), (2).toInt()));
    out["rowGrossA"] = J.s("${'$'}" +J.locDf((J.round(totalARR))));

    out["rowSubCutM"] = J.s("-${'$'}" +J.toFixed((substackFeeM), (2).toInt()));
    out["rowSubCutA"] = J.s("-${'$'}" +J.locDf((J.round(substackFeeA))));

    out["rowStripeM"] = J.s("-${'$'}" +J.toFixed((stripeFeeM), (2).toInt()));
    out["rowStripeA"] = J.s("-${'$'}" +J.locDf((J.round(stripeFeeA))));

    out["rowNetM"] = J.s("${'$'}" +J.toFixed((netMonthly), (2).toInt()));
    out["rowNetA"] = J.s("${'$'}" +J.locDf((J.round(netAnnual))));
    out["rowNetPct"] = J.s(J.toFixed((retentionRate), (1).toInt()) + "%");
  } catch (e: Exception) {
    
  }

    return out
}

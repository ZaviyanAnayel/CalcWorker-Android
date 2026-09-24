package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_cd_ladder_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    val totalDeposit =J.orD((inp.num("cdDeposit")), (50000));
    val rungs =J.orD((J.piD(inp.str("cdRungs"))), (5));
    val apy = (J.orD((inp.num("cdBaseApy")), (4.8))) / 100;
    val compound =J.orD((J.piD(inp.str("cdCompound"))), (12));

    val principalPerRung = totalDeposit / rungs;
    var totalInterest = 0.0;
    var tableHtml = "";

    var yr: Int = 1; while (yr <= rungs) {
      
      val maturityAmount = principalPerRung * J.pw(J.dbl(1 + (apy / compound)), J.dbl(compound * yr));
      val interestEarned = maturityAmount - principalPerRung;
      totalInterest += interestEarned;

      tableHtml += "<tr style=\"border-bottom: 1px solid var(--border);\">" +
        "<td style=\"padding: 10px 14px; color: var(--text-main); font-weight:600;\">" + yr + "-Year CD</td>" +
        "<td style=\"padding: 10px 14px; text-align: right; color: var(--text-main);\">${'$'}" +J.locDf((J.round(principalPerRung))) + "</td>" +
        "<td style=\"padding: 10px 14px; text-align: right; color: #38bdf8;\">+${'$'}" +J.locDf((J.round(interestEarned))) + "</td>" +
        "<td style=\"padding: 10px 14px; text-align: right; font-weight: 700; color: #10b981;\">${'$'}" +J.locDf((J.round(maturityAmount))) + "</td>" +
      "</tr>"; yr += 1}

    val finalBalance = totalDeposit + totalInterest;

    out["kpiTotalInterest"] = J.s("${'$'}" +J.locDf((J.round(totalInterest))));
    out["kpiAnnualLiq"] = J.s("${'$'}" +J.locDf((J.round(principalPerRung))));
    out["subAnnualLiq"] = J.s("Unlocks every 12 months");
    out["kpiBlendedApy"] = J.s(J.toFixed(((apy * 100)), (2).toInt()) + "%");
    out["kpiFinalBalance"] = J.s("${'$'}" +J.locDf((J.round(finalBalance))));
  } catch (e: Exception) {
    
  }

    return out
}

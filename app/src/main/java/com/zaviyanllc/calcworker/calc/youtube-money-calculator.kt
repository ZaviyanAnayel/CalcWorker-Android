package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_youtube_money_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    try {
      val longViews =J.orD((inp.num("monthlyViews")), (0));
      val shortsViews =J.orD((inp.num("shortsViews")), (0));
      val rpm =J.orD((inp.num("longformRPM")), (4.50));
      val sponsors =J.orD((inp.num("sponsorshipIncome")), (0));

      out["rpmDisplay"] = J.s("${'$'}" +J.toFixed((rpm), (2).toInt()));

      val longRev = (longViews / 1000) * rpm;
      val shortsRev = (shortsViews / 1000) * 0.07; 
      val grossMonthly = longRev + shortsRev + sponsors;
      val annualGross = grossMonthly * 12;
      val taxReserve = grossMonthly * 0.25;
      val netTakeHome = grossMonthly - taxReserve;

      out["kpiTotalEarnings"] = J.s("${'$'}" +J.locDf((J.round(grossMonthly))));
      out["annualSub"] = J.s("Estimated Annual: ${'$'}" +J.locDf((J.round(annualGross))) + " / yr");
      out["kpiLongformAdSense"] = J.s("${'$'}" +J.locDf((J.round(longRev))));
      out["kpiShortsRevenue"] = J.s("${'$'}" +J.locDf((J.round(shortsRev))));
      out["kpiSponsorships"] = J.s("${'$'}" +J.locDf((J.round(sponsors))));
      out["kpiNetTakeHome"] = J.s("${'$'}" +J.locDf((J.round(netTakeHome))));

      out["rowLongform"] = J.s("${'$'}" +J.toFixed((longRev), (2).toInt()));
      out["rowShorts"] = J.s("${'$'}" +J.toFixed((shortsRev), (2).toInt()));
      out["rowSponsors"] = J.s("${'$'}" +J.toFixed((sponsors), (2).toInt()));
      out["rowGross"] = J.s("${'$'}" +J.toFixed((grossMonthly), (2).toInt()));
      out["rowTax"] = J.s("-${'$'}" +J.toFixed((taxReserve), (2).toInt()));
      out["rowNet"] = J.s("${'$'}" +J.toFixed((netTakeHome), (2).toInt()));
    } catch (err: Exception) {
      
    }
  
    return out
}

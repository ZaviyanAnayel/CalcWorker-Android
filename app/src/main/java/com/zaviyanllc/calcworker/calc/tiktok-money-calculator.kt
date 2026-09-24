package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_tiktok_money_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    try {
      val views =J.orD((inp.num("videoViews")), (0));
      val qualRate =J.orD((inp.num("qualifiedRate")), (55));
      val rpm =J.orD((inp.num("rpmRate")), (0.70));
      val liveDiamonds =J.orD((inp.num("liveDiamonds")), (0));
      val brandDeals =J.orD((inp.num("brandDeals")), (0));

      out["qualifiedDisplay"] = J.s(qualRate + "%");
      out["rpmDisplay"] = J.s("${'$'}" +J.toFixed((rpm), (2).toInt()));

      val qualifiedViews = views * (qualRate / 100);
      val creatorRewards = (qualifiedViews / 1000) * rpm;
      val liveCash = liveDiamonds * 0.005;
      val totalGross = creatorRewards + liveCash + brandDeals;
      val annualGross = totalGross * 12;
      val taxes = totalGross * 0.25;
      val netTakeHome = totalGross - taxes;

      out["kpiTotalEarnings"] = J.s("${'$'}" +J.locDf((J.round(totalGross))));
      out["annualSub"] = J.s("Est. Annual: ${'$'}" +J.locDf((J.round(annualGross))) + " / yr");
      out["kpiCreatorRewards"] = J.s("${'$'}" +J.locDf((J.round(creatorRewards))));
      out["kpiLiveGifts"] = J.s("${'$'}" +J.locDf((J.round(liveCash))));
      out["kpiBrandDeals"] = J.s("${'$'}" +J.locDf((J.round(brandDeals))));
      out["kpiNetTakeHome"] = J.s("${'$'}" +J.locDf((J.round(netTakeHome))));

      out["rowRewards"] = J.s("${'$'}" +J.toFixed((creatorRewards), (2).toInt()));
      out["rowDiamonds"] = J.s("${'$'}" +J.toFixed((liveCash), (2).toInt()));
      out["rowSponsors"] = J.s("${'$'}" +J.toFixed((brandDeals), (2).toInt()));
      out["rowGross"] = J.s("${'$'}" +J.toFixed((totalGross), (2).toInt()));
      out["rowTax"] = J.s("-${'$'}" +J.toFixed((taxes), (2).toInt()));
      out["rowNet"] = J.s("${'$'}" +J.toFixed((netTakeHome), (2).toInt()));
    } catch (err: Exception) {
      
    }
  
    return out
}

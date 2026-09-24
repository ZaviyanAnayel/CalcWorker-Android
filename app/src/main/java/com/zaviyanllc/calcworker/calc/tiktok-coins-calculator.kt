package com.zaviyanllc.calcworker.calc

import kotlin.math.*

private var activePlatform: String = "web"

fun calc_tiktok_coins_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    try {
      
      val coins =J.orD(inp.num("coinInput"), 0.0);

      val webCost = coins * 0.0105;
      val appCost = coins * 0.0150;
      val buyerCost =(if (activePlatform == "web") webCost else appCost);
      val savings = kotlin.math.abs(appCost - webCost);

      val diamonds = coins * 0.5; 
      val creatorCash = diamonds * 0.005; 
      val platformFee = J.max(0, buyerCost - creatorCash);
      val takePercent =(if (buyerCost > 0) (creatorCash / buyerCost) * 100 else 0.0);

      out["kpiBuyerCost"] = J.s("${'$'}" +J.loc((buyerCost), (2).toInt(), (2).toInt()));
      out["kpiDiamonds"] = J.s(J.locDf((J.round(diamonds))) + " 💎");
      out["kpiCreatorCash"] = J.s("${'$'}" +J.loc((creatorCash), (2).toInt(), (2).toInt()));
      out["kpiTikTokFee"] = J.s("${'$'}" +J.loc((platformFee), (2).toInt(), (2).toInt()));
      out["kpiTakePercent"] = J.s(J.toFixed((takePercent), (1).toInt()) + "%");

      
      out["rowWebCost"] = J.s("${'$'}" +J.loc((webCost), (2).toInt(), (2).toInt()));
      out["rowAppCost"] = J.s("${'$'}" +J.loc((appCost), (2).toInt(), (2).toInt()));
      out["rowDiamonds"] = J.s(J.locDf((J.round(diamonds))) + " Diamonds");
      out["rowRate"] = J.s("${'$'}0.005");
      out["rowCashout"] = J.s("${'$'}" +J.loc((creatorCash), (2).toInt(), (2).toInt()));
    } catch (err: Exception) {
      
    }
  
    return out
}

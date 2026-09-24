package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_etsy_profit(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

      val price =J.orD((inp.num("itemPrice")), (0));
      val shipCharged =J.orD((inp.num("shippingCharged")), (0));
      val itemCost =J.orD((inp.num("itemCost")), (0));
      val shipCost =J.orD((inp.num("shippingCost")), (0));
      val offsiteRate =J.orD((inp.num("offsiteAds")), (0));

      val totalRevenue = price + shipCharged;

      
      val listingFee = 0.20;
      val transactionFee = totalRevenue * 0.065;
      val paymentFee =(if (totalRevenue > 0) (totalRevenue * 0.03) + 0.25 else 0.0);
      val offsiteFee = totalRevenue * offsiteRate;
      val totalFees = listingFee + transactionFee + paymentFee + offsiteFee;

      val totalCosts = itemCost + shipCost;
      val netProfit = totalRevenue - totalFees - totalCosts;
      val profitMargin =(if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0);
      val roi =(if (totalCosts > 0) (netProfit / totalCosts) * 100 else 0.0);
      val feePct =(if (totalRevenue > 0) (totalFees / totalRevenue) * 100 else 0.0);

      
      
      out["resNetProfit"] = J.s("${'$'}${J.toFixed((netProfit), (2).toInt())}");

      out["resMargin"] = J.s("Profit Margin: ${J.toFixed((profitMargin), (1).toInt())}%");
      out["resTotalRev"] = J.s("${'$'}${J.toFixed((totalRevenue), (2).toInt())}");
      out["resTotalFees"] = J.s("-${'$'}${J.toFixed((totalFees), (2).toInt())}");
      out["resTotalCost"] = J.s("-${'$'}${J.toFixed((totalCosts), (2).toInt())}");
      out["resROI"] = J.s("${J.toFixed((roi), (1).toInt())}%");
      out["resFeePct"] = J.s("${J.toFixed((feePct), (1).toInt())}%");

      
      out["feeListing"] = J.s("${'$'}${J.toFixed((listingFee), (2).toInt())}");
      out["feeTransaction"] = J.s("${'$'}${J.toFixed((transactionFee), (2).toInt())}");
      out["feePayment"] = J.s("${'$'}${J.toFixed((paymentFee), (2).toInt())}");
      out["feeOffsite"] = J.s("${'$'}${J.toFixed((offsiteFee), (2).toInt())}");
      out["offsiteRateLabel"] = J.s("${J.toFixed(((offsiteRate * 100)), (0).toInt())}%");
    
    return out
}

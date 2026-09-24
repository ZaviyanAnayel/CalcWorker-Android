package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_shopify_fee_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    try {
      val plan =J.orS((inp.str("planSelect")), ("basic"));
      val revenue =J.orD((inp.num("monthlyRevenue")), (0));
      val aov = J.max(1,J.orD((inp.num("aov")), (50)));
      val cogsPct =J.orD((inp.num("cogsRate")), (30));
      val shipping =J.orD((inp.num("shippingCharged")), (0));
      val ads =J.orD((inp.num("adSpend")), (0));
      val apps =J.orD((inp.num("appsCost")), (0));

      out["cogsDisplay"] = J.s(J.toFixed((cogsPct), (1).toInt()) + "%");

      val orders = J.round(revenue / aov);

      var planFee = 39.0;
      var ratePct = 0.029;
      var rateFixed = 0.30;
      if (plan == "shopify") {
        planFee = 105.0;
        ratePct = 0.027;
      } else if (plan == "advanced") {
        planFee = 399.0;
        ratePct = 0.025;
      }

      val gatewayFees = (revenue * ratePct) + (orders * rateFixed);
      val totalShopifyFees = planFee + gatewayFees;
      val cogsTotal = revenue * (cogsPct / 100);
      val totalCosts = totalShopifyFees + apps + cogsTotal + shipping + ads;
      val netProfit = revenue - totalCosts;
      val netMargin =(if (revenue > 0) (netProfit / revenue) * 100 else 0.0);
      val effectiveFeePct =(if (revenue > 0) (totalShopifyFees / revenue) * 100 else 0.0);

      val currentCAC =(if (orders > 0) (ads / orders) else 0.0);
      val grossMarginPerOrder = aov - (aov * (cogsPct / 100)) - (aov * ratePct + rateFixed) - (shipping / J.max(1, orders));
      val breakEvenCAC = J.max(0, grossMarginPerOrder);

      out["kpiNetProfit"] = J.s("${'$'}" +J.toFixed((netProfit), (2).toInt()));
      out["marginSub"] = J.s("Net Margin: " +J.toFixed((netMargin), (1).toInt()) + "% | Orders: " +J.locDf((orders)));
      out["kpiShopifyFees"] = J.s("${'$'}" +J.toFixed((totalShopifyFees), (2).toInt()));
      out["kpiEffectiveFee"] = J.s(J.toFixed((effectiveFeePct), (2).toInt()) + "%");
      out["kpiCAC"] = J.s("${'$'}" +J.toFixed((currentCAC), (2).toInt()));
      out["kpiBreakCAC"] = J.s("${'$'}" +J.toFixed((breakEvenCAC), (2).toInt()));

      out["rowGross"] = J.s("${'$'}" +J.toFixed((revenue), (2).toInt()));
      out["rowPlanFee"] = J.s("-${'$'}" +J.toFixed((planFee), (2).toInt()));
      out["rowGatewayFee"] = J.s("-${'$'}" +J.toFixed((gatewayFees), (2).toInt()));
      out["rowApps"] = J.s("-${'$'}" +J.toFixed((apps), (2).toInt()));
      out["rowCOGS"] = J.s("-${'$'}" +J.toFixed((cogsTotal), (2).toInt()));
      out["rowShipping"] = J.s("-${'$'}" +J.toFixed((shipping), (2).toInt()));
      out["rowAds"] = J.s("-${'$'}" +J.toFixed((ads), (2).toInt()));
      out["rowNet"] = J.s("${'$'}" +J.toFixed((netProfit), (2).toInt()));
    } catch (err: Exception) {
      
    }
  
    return out
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_tiktok_shop_affiliate_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    try {
      val price =J.orD((inp.num("productPrice")), (0));
      val commRate =J.orD((inp.num("commissionRate")), (15));
      val units =J.orD((inp.num("unitsSold")), (0));
      val sample =J.orD((inp.num("sampleCost")), (0));
      val returnRate =J.orD((inp.num("returnRate")), (8));
      val taxRate =J.orD((inp.num("taxRate")), (25));

      out["commDisplay"] = J.s(J.toFixed((commRate), (1).toInt()) + "%");
      out["returnDisplay"] = J.s(J.toFixed((returnRate), (1).toInt()) + "%");
      out["taxDisplay"] = J.s(taxRate + "%");

      val gmv = price * units;
      val grossComm = gmv * (commRate / 100);
      val returnDeduction = grossComm * (returnRate / 100);
      val platformPayout = J.max(0, grossComm - returnDeduction - sample);
      val taxReserve = platformPayout * (taxRate / 100);
      val netProfit = platformPayout - taxReserve;
      val unitComm = price * (commRate / 100);

      out["kpiNetPayout"] = J.s("${'$'}" +J.toFixed((platformPayout), (2).toInt()));
      out["gmvSub"] = J.s("Gross GMV Generated: ${'$'}" +J.loc((gmv), (2).toInt(), (2).toInt()));
      out["kpiUnitComm"] = J.s("${'$'}" +J.toFixed((unitComm), (2).toInt()));
      out["kpiGrossComm"] = J.s("${'$'}" +J.toFixed((grossComm), (2).toInt()));
      out["kpiReturnDeduct"] = J.s("-${'$'}" +J.toFixed((returnDeduction), (2).toInt()));
      out["kpiAfterTax"] = J.s("${'$'}" +J.toFixed((netProfit), (2).toInt()));

      out["rowGMV"] = J.s("${'$'}" +J.toFixed((gmv), (2).toInt()));
      out["rowGrossComm"] = J.s("${'$'}" +J.toFixed((grossComm), (2).toInt()));
      out["rowReturns"] = J.s("-${'$'}" +J.toFixed((returnDeduction), (2).toInt()));
      out["rowSample"] = J.s("-${'$'}" +J.toFixed((sample), (2).toInt()));
      out["rowPayout"] = J.s("${'$'}" +J.toFixed((platformPayout), (2).toInt()));
      out["rowTax"] = J.s("-${'$'}" +J.toFixed((taxReserve), (2).toInt()));
      out["rowNet"] = J.s("${'$'}" +J.toFixed((netProfit), (2).toInt()));
    } catch (err: Exception) {
      
    }
  
    return out
}

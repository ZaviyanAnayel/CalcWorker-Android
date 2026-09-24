package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_ebay_fee_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var price =J.orD((inp.num("itemPrice")), (0));
  var shipCh =J.orD((inp.num("shipCharged")), (0));
  var shipAc =J.orD((inp.num("shipActual")), (0));
  var cogs =J.orD((inp.num("itemCogs")), (0));
  var catRate =J.orD((inp.num("categorySelect")), (0.1325));
  var adRate = (J.orD((inp.num("adRate")), (0))) / 100;

  var totalRev = price + shipCh;
  var fvf = (totalRev * catRate) + 0.30;
  var adFee = totalRev * adRate;
  var totalFees = fvf + adFee;
  var shipNet = shipCh - shipAc;
  var netProfit = totalRev - totalFees - shipAc - cogs;
  var margin =(if (totalRev > 0) ((netProfit / totalRev) * 100) else 0.0);

  out["resNetProfit"] = J.s(ebay_fee_calculator_fmt(netProfit));
  out["resMargin"] = J.s("Net Profit Margin: " +J.toFixed((margin), (1).toInt()) + "%");
  out["resTotalRev"] = J.s(ebay_fee_calculator_fmt(totalRev));
  out["resFvfFee"] = J.s("-" + ebay_fee_calculator_fmt(fvf));
  out["resAdFee"] = J.s("-" + ebay_fee_calculator_fmt(adFee));
  out["resShipNet"] = J.s(((if (shipNet >= 0) "+" else "")) + ebay_fee_calculator_fmt(shipNet));
  out["resTotalFees"] = J.s("-" + ebay_fee_calculator_fmt(totalFees));

    return out
}

private fun ebay_fee_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.loc((n), (2).toInt(), (2).toInt());
}

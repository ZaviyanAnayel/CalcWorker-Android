package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_crypto_profit_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var invest =J.orD((inp.num("investAmt")), (0));
  var buyP =J.orD((inp.num("buyPrice")), (1));
  var sellP =J.orD((inp.num("sellPrice")), (0));
  var feeRate = (J.orD((inp.num("feeRate")), (0))) / 100;

  var coins = invest / buyP;
  var grossExit = coins * sellP;
  var entryFee = invest * feeRate;
  var exitFee = grossExit * feeRate;
  var totalFees = entryFee + exitFee;
  var netExit = grossExit - totalFees;
  var profit = netExit - invest;
  var roi =(if (invest > 0) ((profit / invest) * 100) else 0.0);

  out["resNetProfit"] = J.s(((if (profit >= 0) "+" else "")) + crypto_profit_calculator_fmt(profit));
  
  out["resRoiLabel"] = J.s("Total ROI: " + ((if (roi >= 0) "+" else "")) +J.toFixed((roi), (1).toInt()) + "%");
  out["resCoins"] = J.s(J.toFixed((coins), (6).toInt()) + " Coins");
  out["resInitial"] = J.s(crypto_profit_calculator_fmt(invest));
  out["resFees"] = J.s("-" + crypto_profit_calculator_fmt(totalFees));
  out["resExitVal"] = J.s(crypto_profit_calculator_fmt(netExit));

    return out
}

private fun crypto_profit_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.loc((n), (2).toInt(), (2).toInt());
}

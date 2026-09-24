package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_inflation_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var amt =J.orD((inp.num("startAmount")), (0));
  var yrs =J.orD((J.piD(inp.str("yearsSpan"))), (1));
  var rate = (J.orD((inp.num("inflationRate")), (0))) / 100;

  var multiplier = J.pw(J.dbl(1 + rate), J.dbl(yrs));
  var futureNeeded = amt * multiplier;
  var cumPercent = (multiplier - 1) * 100;

  var futureCashVal = amt / multiplier;
  var lostVal = amt - futureCashVal;
  var lostPercent = (lostVal / J.max(1, amt)) * 100;

  out["resFutureNeeded"] = J.s(inflation_calculator_fmt(futureNeeded));
  out["resLossSummary"] = J.s("To buy in " + yrs + " years what " + inflation_calculator_fmt(amt) + " buys today");
  out["resStart"] = J.s(inflation_calculator_fmt(amt));
  out["resCumRate"] = J.s("+" +J.toFixed((cumPercent), (1).toInt()) + "%");
  out["resCashPower"] = J.s(inflation_calculator_fmt(futureCashVal));
  out["resLostPower"] = J.s("-" + inflation_calculator_fmt(lostVal) + " (-" +J.toFixed((lostPercent), (1).toInt()) + "%)");

    return out
}

private fun inflation_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.locDf((J.round(n)));
}

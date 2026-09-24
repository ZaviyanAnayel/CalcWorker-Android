package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_heloc_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var hv =J.orD((inp.num("homeVal")), (0));
  var fm =J.orD((inp.num("firstMortgage")), (0));
  var cltv = (J.orD((inp.num("maxCltv")), (80))) / 100;
  var apr = (J.orD((inp.num("helocApr")), (0))) / 100;
  var draw =J.orD((inp.num("drawAmount")), (0));

  var maxBorrow = hv * cltv;
  var maxLine = J.max(0, maxBorrow - fm);
  var totalEquity = J.max(0, hv - fm);

  var actualDraw = J.min(draw, maxLine);
  var monthlyRate = apr / 12;
  var ioPay = actualDraw * monthlyRate;

  
  var n = 240.0;
  var repayPay =(if ((monthlyRate > 0)) (actualDraw * (monthlyRate * J.pw(J.dbl(1+monthlyRate), J.dbl(n))) / (J.pw(J.dbl(1+monthlyRate), J.dbl(n)) - 1)) else (actualDraw / n));

  out["resMaxLine"] = J.s(heloc_calculator_fmt(maxLine));
  out["resDrawStatus"] = J.s("Simulating " + heloc_calculator_fmt(actualDraw) + " borrowed (" +J.toFixed((((actualDraw/J.max(1, maxLine))*100)), (0).toInt()) + "% of limit)");
  out["resHomeVal"] = J.s(heloc_calculator_fmt(hv));
  out["resTotalEquity"] = J.s(heloc_calculator_fmt(totalEquity));
  out["resFirstMort"] = J.s(heloc_calculator_fmt(fm));
  out["resIoPay"] = J.s(heloc_calculator_fmt(ioPay) + "/mo");
  out["resRepayPay"] = J.s(heloc_calculator_fmt(repayPay) + "/mo");

    return out
}

private fun heloc_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.locDf((J.round(n)));
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_overtime_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var rate =J.orD((inp.num("hourlyRate")), (0));
  var regH =J.orD((inp.num("regHours")), (0));
  var otH =J.orD((inp.num("otHours")), (0));
  var dtH =J.orD((inp.num("dtHours")), (0));

  var regPay = regH * rate;
  var otPay = otH * rate * 1.5;
  var dtPay = dtH * rate * 2.0;
  var totalGross = regPay + otPay + dtPay;
  var totalH = regH + otH + dtH;
  var blendedRate =(if (totalH > 0) (totalGross / totalH) else rate);

  out["resGrossTotal"] = J.s(overtime_calculator_fmt(totalGross));
  out["resBlendedRate"] = J.s("Effective Blended Rate: " + overtime_calculator_fmt(blendedRate) + "/hr (" + totalH + " Total Hours)");
  out["resRegPay"] = J.s(overtime_calculator_fmt(regPay));
  out["resOtPay"] = J.s("+" + overtime_calculator_fmt(otPay));
  out["resDtPay"] = J.s("+" + overtime_calculator_fmt(dtPay));
  out["resAnnualProj"] = J.s(overtime_calculator_fmt(totalGross * 52) + " / yr");

    return out
}

private fun overtime_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.loc((n), (2).toInt(), (2).toInt());
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_tip_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  val bill=J.orD((inp.num("billAmount")), (0));
  val pct=J.orD((inp.num("tipPct")), (0));
  val people=J.orD((J.piD(inp.str("numPeople"))), (1));
  val doRound=inp.bool("roundUp");
  val tip=bill*pct/100;
  val total=bill+tip;
  val perBill=bill/people;
  val perTip=tip/people;
  var perTotal=total/people;
  if(doRound)perTotal=kotlin.math.ceil(perTotal);
  out["perPersonTotal"] = J.s("${'$'}"+tip_calculator_fmt(perTotal));
  out["perPersonBreakdown"] = J.s("${'$'}"+tip_calculator_fmt(perBill)+" + ${'$'}"+tip_calculator_fmt(perTip)+" tip");
  out["resBillAmt"] = J.s("${'$'}"+tip_calculator_fmt(bill));
  out["resTipAmt"] = J.s("${'$'}"+tip_calculator_fmt(tip));
  out["resTotalBill"] = J.s("${'$'}"+tip_calculator_fmt(total));
  out["resPerBill"] = J.s("${'$'}"+tip_calculator_fmt(perBill));
  out["resPerTip"] = J.s("${'$'}"+tip_calculator_fmt(perTip));
  out["resPerTotal"] = J.s("${'$'}"+tip_calculator_fmt(perTotal));

    return out
}

private fun tip_calculator_fmt(n: Number): String {
    var n = n.toDouble();return J.loc((n), (2).toInt(), (2).toInt());
}

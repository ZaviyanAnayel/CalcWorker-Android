package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_roth_ira_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var curAge =J.orD((J.piD(inp.str("currentAge"))), (25));
  var retAge =J.orD((J.piD(inp.str("retireAge"))), (65));
  var bal =J.orD((inp.num("startBal")), (0));
  var contrib =J.orD((inp.num("annualContrib")), (0));
  var r = (J.orD((inp.num("annualReturn")), (0))) / 100;

  var years = J.max(1, retAge - curAge);
  var currentBal = bal;
  var totalInvested = bal;

  var i: Int = 0; while (i<years) {
    currentBal = (currentBal + contrib) * (1 + r);
    totalInvested += contrib; i += 1}

  var growth = J.max(0, currentBal - totalInvested);
  var monthlyIncome = (currentBal * 0.04) / 12;
  var estimatedTaxSaved = growth * 0.25;

  out["resNestEgg"] = J.s(roth_ira_calculator_fmt(currentBal));
  out["resRetireLabel"] = J.s("At Age " + retAge + " (" + years + " Years of Compounding)");
  out["resTotalContrib"] = J.s(roth_ira_calculator_fmt(totalInvested));
  out["resTotalGrowth"] = J.s("+" + roth_ira_calculator_fmt(growth));
  out["resMonthlyIncome"] = J.s(roth_ira_calculator_fmt(monthlyIncome) + "/mo");
  out["resTaxSaved"] = J.s("+" + roth_ira_calculator_fmt(estimatedTaxSaved));

    return out
}

private fun roth_ira_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.locDf((J.round(n)));
}

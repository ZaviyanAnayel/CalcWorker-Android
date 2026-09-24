package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_podcast_sponsorship_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var dl =J.orD((inp.num("epDownloads")), (0));
  var epMonth =J.orD((J.piD(inp.str("epPerMonth"))), (1));
  var midSlots =J.orD((J.piD(inp.str("midSlots"))), (0));
  var midCpm =J.orD((inp.num("midCpm")), (25));
  var preCpm =J.orD((inp.num("preRollSelect")), (0));

  var thousands = dl / 1000;
  var midPerEp = thousands * midCpm * midSlots;
  var prePerEp =(if (preCpm > 0) (thousands * preCpm) else 0.0);
  var epTotal = midPerEp + prePerEp;
  var monthlyTotal = epTotal * epMonth;

  out["resMonthlySponsor"] = J.s(podcast_sponsorship_calculator_fmt(monthlyTotal) + "/mo");
  out["resEpSummary"] = J.s(podcast_sponsorship_calculator_fmt(epTotal) + " per episode (" + epMonth + " episodes / month)");
  out["resPerEp"] = J.s(podcast_sponsorship_calculator_fmt(epTotal));
  out["resMidTotal"] = J.s(podcast_sponsorship_calculator_fmt(midPerEp));
  out["resPreTotal"] = J.s(podcast_sponsorship_calculator_fmt(prePerEp));
  out["resAnnualSponsor"] = J.s(podcast_sponsorship_calculator_fmt(monthlyTotal * 12) + " / yr");

    return out
}

private fun podcast_sponsorship_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.loc((n), (2).toInt(), (2).toInt());
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_sponsorship_pricing_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("views" to J.dbl(inp.num("views")), "cpm" to J.dbl(inp.num("cpm")), "followers" to J.dbl(inp.num("followers")), "cents" to J.dbl(inp.num("cents")), "deliv" to J.dbl(inp.num("deliv"))))
  var r=sponsorship_pricing_calculator_core(p);
  out["resRange"] = J.s(sponsorship_pricing_calculator_fmtM(r.num("lo"))+" – "+sponsorship_pricing_calculator_fmtM(r.num("hi")));
  out["resRangeSub"] = J.s("per deliverable");
  out["resCPM"] = J.s(sponsorship_pricing_calculator_fmtM(r.num("cpmRate")));out["resFol"] = J.s(sponsorship_pricing_calculator_fmtM(r.num("folRate")));
  out["resPack"] = J.s(sponsorship_pricing_calculator_fmtM(r.num("packLo"))+" – "+sponsorship_pricing_calculator_fmtM(r.num("packHi")));
  out["resAsk"] = J.s(sponsorship_pricing_calculator_fmtM(r.num("ask"))+" (quote high)");
  out["shareText"] = J.s("Sponsorship range "+sponsorship_pricing_calculator_fmtM(r.num("lo"))+"–"+sponsorship_pricing_calculator_fmtM(r.num("hi"))+" per post");

    return out
}

private fun sponsorship_pricing_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+sponsorship_pricing_calculator_fmt0(kotlin.math.abs(n));
}

private fun sponsorship_pricing_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun sponsorship_pricing_calculator_core(p: Inp): Inp {
    var p = p;
  var cpmRate=p.num("views")/1000*p.num("cpm");
  var folRate=p.num("followers")*p.num("cents")/100;
  var lo=J.min(cpmRate,folRate); var hi=J.max(cpmRate,folRate);
  var ask=hi;
  return Inp(mapOf("cpmRate" to J.dbl(cpmRate), "folRate" to J.dbl(folRate), "lo" to J.dbl(lo), "hi" to J.dbl(hi), "ask" to J.dbl(ask), "packLo" to J.dbl(lo*p.num("deliv")), "packHi" to J.dbl(hi*p.num("deliv"))));

}

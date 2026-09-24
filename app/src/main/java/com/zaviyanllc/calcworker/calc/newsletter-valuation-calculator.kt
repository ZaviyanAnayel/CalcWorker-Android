package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_newsletter_valuation_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("subs" to J.dbl(inp.num("subs")), "paidPct" to J.dbl(inp.num("paidPct")), "arpu" to J.dbl(inp.num("arpu")), "multLo" to J.dbl(inp.num("multLo")), "multHi" to J.dbl(inp.num("multHi"))))
  var r=newsletter_valuation_calculator_core(p);
  out["resVal"] = J.s(newsletter_valuation_calculator_fmtM(r.num("lo"))+" – "+newsletter_valuation_calculator_fmtM(r.num("hi")));
  out["resValSub"] = J.s(p.num("multLo")+"× – "+p.num("multHi")+"× ARR");
  out["resPaid"] = J.s(newsletter_valuation_calculator_fmt0(r.num("paid")));out["resARR"] = J.s(newsletter_valuation_calculator_fmtM(r.num("arr"))+"/yr");
  out["resLo"] = J.s(newsletter_valuation_calculator_fmtM(r.num("lo")));out["resHi"] = J.s(newsletter_valuation_calculator_fmtM(r.num("hi")));
  out["resPerSub"] = J.s("${'$'}"+J.toFixed((r.num("perLo")), (2).toInt())+" – ${'$'}"+J.toFixed((r.num("perHi")), (2).toInt()));
  out["shareText"] = J.s("Newsletter valuation "+newsletter_valuation_calculator_fmtM(r.num("lo"))+"–"+newsletter_valuation_calculator_fmtM(r.num("hi"))+" ("+newsletter_valuation_calculator_fmtM(r.num("arr"))+" ARR)");

    return out
}

private fun newsletter_valuation_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun newsletter_valuation_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+newsletter_valuation_calculator_fmt0(kotlin.math.abs(n));
}

private fun newsletter_valuation_calculator_core(p: Inp): Inp {
    var p = p;
  var paid=p.num("subs")*p.num("paidPct")/100;
  var arr=paid*p.num("arpu");
  return Inp(mapOf("paid" to J.dbl(paid), "arr" to J.dbl(arr), "lo" to J.dbl(arr*p.num("multLo")), "hi" to J.dbl(arr*p.num("multHi")), "perLo" to J.dbl((if (p.num("subs")>0) arr*p.num("multLo")/p.num("subs") else 0)), "perHi" to J.dbl((if (p.num("subs")>0) arr*p.num("multHi")/p.num("subs") else 0))));

}

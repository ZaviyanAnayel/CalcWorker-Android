package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_youtube_shorts_earnings_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("views" to J.dbl(inp.num("views")), "rpm" to J.dbl(inp.num("rpm")), "months" to J.dbl(inp.num("months"))))
  var r=youtube_shorts_earnings_calculator_core(p);
  out["resMo"] = J.s(youtube_shorts_earnings_calculator_fmtM(r.num("mo")));
  out["resMoSub"] = J.s("At ${'$'}"+J.toFixed((p.num("rpm")), (2).toInt())+" RPM");
  out["resProj"] = J.s(youtube_shorts_earnings_calculator_fmtM(r.num("proj"))+" / "+p.num("months")+" mo");
  out["resPerM"] = J.s(youtube_shorts_earnings_calculator_fmtM(r.num("perM")));
  out["resVs"] = J.s("Long-form pays ~"+J.round(r.num("vs"))+"× more");
  out["shareText"] = J.s("Shorts earnings "+youtube_shorts_earnings_calculator_fmtM(r.num("mo"))+"/mo at "+youtube_shorts_earnings_calculator_fmtM(p.num("views"))+" views");

    return out
}

private fun youtube_shorts_earnings_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+youtube_shorts_earnings_calculator_fmt0(kotlin.math.abs(n));
}

private fun youtube_shorts_earnings_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun youtube_shorts_earnings_calculator_core(p: Inp): Inp {
    var p = p;
  var mo=p.num("views")/1000*p.num("rpm");
  var perM=1000*p.num("rpm");
  var vs=(if (p.num("rpm")>0) 4/p.num("rpm") else 0);
  return Inp(mapOf("mo" to J.dbl(mo), "proj" to J.dbl(mo*p.num("months")), "perM" to J.dbl(perM), "vs" to J.dbl(vs)));

}

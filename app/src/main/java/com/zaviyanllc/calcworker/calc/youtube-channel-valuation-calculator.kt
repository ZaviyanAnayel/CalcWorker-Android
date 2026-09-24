package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_youtube_channel_valuation_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("rev" to J.dbl(inp.num("rev")), "views" to J.dbl(inp.num("views")), "multLo" to J.dbl(inp.num("multLo")), "multHi" to J.dbl(inp.num("multHi"))))
  var r=youtube_channel_valuation_calculator_core(p);
  out["resVal"] = J.s(youtube_channel_valuation_calculator_fmtM(r.num("lo"))+" – "+youtube_channel_valuation_calculator_fmtM(r.num("hi")));
  out["resValSub"] = J.s(p.num("multLo")+"× – "+p.num("multHi")+"× monthly revenue");
  out["resLo"] = J.s(youtube_channel_valuation_calculator_fmtM(r.num("lo")));out["resHi"] = J.s(youtube_channel_valuation_calculator_fmtM(r.num("hi")));
  out["resAnnual"] = J.s(youtube_channel_valuation_calculator_fmtM(r.num("annual"))+"/yr");out["resRPM"] = J.s("${'$'}"+J.toFixed((r.num("rpm")), (2).toInt()));
  out["resPerV"] = J.s(youtube_channel_valuation_calculator_fmtM(r.num("perV")));
  out["shareText"] = J.s("Channel valuation "+youtube_channel_valuation_calculator_fmtM(r.num("lo"))+"–"+youtube_channel_valuation_calculator_fmtM(r.num("hi")));

    return out
}

private fun youtube_channel_valuation_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+youtube_channel_valuation_calculator_fmt0(kotlin.math.abs(n));
}

private fun youtube_channel_valuation_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun youtube_channel_valuation_calculator_core(p: Inp): Inp {
    var p = p;
  return Inp(mapOf("lo" to J.dbl(p.num("rev")*p.num("multLo")), "hi" to J.dbl(p.num("rev")*p.num("multHi")), "annual" to J.dbl(p.num("rev")*12), "rpm" to J.dbl((if (p.num("views")>0) p.num("rev")/p.num("views")*1000 else 0)), "perV" to J.dbl((if (p.num("views")>0) p.num("rev")*p.num("multHi")/(p.num("views")/1000) else 0))));

}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_ugc_creator_rate_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("videos" to J.dbl(inp.num("videos")), "base" to J.dbl(inp.num("base")), "usage" to J.dbl(inp.num("usage")), "revisions" to J.dbl(inp.num("revisions")), "revFee" to J.dbl(inp.num("revFee")), "rushPct" to J.dbl(inp.num("rushPct"))))
  var r=ugc_creator_rate_calculator_core(p);
  out["resTotal"] = J.s(ugc_creator_rate_calculator_fmtM(r.num("total")));
  out["resPerVideo"] = J.s(ugc_creator_rate_calculator_fmtM(r.num("perVideo"))+" per video");
  out["resBase"] = J.s(ugc_creator_rate_calculator_fmtM(r.num("base")));out["resMult"] = J.s(r.num("mult")+"×");
  out["resRev"] = J.s(ugc_creator_rate_calculator_fmtM(r.num("rev")));out["resRush"] = J.s(ugc_creator_rate_calculator_fmtM(r.num("rush")));
  out["shareText"] = J.s("UGC package: "+ugc_creator_rate_calculator_fmtM(r.num("total"))+" for "+p.num("videos")+" videos");

    return out
}

private fun ugc_creator_rate_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+ugc_creator_rate_calculator_fmt0(kotlin.math.abs(n));
}

private fun ugc_creator_rate_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun ugc_creator_rate_calculator_core(p: Inp): Inp {
    var p = p;
  var base=p.num("videos")*p.num("base");
  var withUsage=base*p.num("usage");
  var rev=p.num("revisions")*p.num("revFee");
  var rush=(withUsage+rev)*p.num("rushPct")/100;
  return Inp(mapOf("total" to J.dbl(withUsage+rev+rush), "perVideo" to J.dbl((if (p.num("videos")>0) (withUsage+rev+rush)/p.num("videos") else 0)), "base" to J.dbl(base), "mult" to J.dbl(p.num("usage")), "rev" to J.dbl(rev), "rush" to J.dbl(rush)));

}

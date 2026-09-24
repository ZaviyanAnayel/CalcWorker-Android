package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_cap_rate_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("price" to J.dbl(inp.num("price")), "rent" to J.dbl(inp.num("rent")), "tax" to J.dbl(inp.num("tax")), "ins" to J.dbl(inp.num("ins")), "maint" to J.dbl(inp.num("maint")), "mgmtPct" to J.dbl(inp.num("mgmtPct")), "vacPct" to J.dbl(inp.num("vacPct"))))
  var r=cap_rate_calculator_core(p);
  out["resCap"] = J.s(cap_rate_calculator_fmtP(r.num("cap"),2));
  out["resCapSub"] = J.s((if (r.num("cap")>=8) "High yield — verify risk" else ((if (r.num("cap")>=5) "Healthy cash-flow range" else "Low yield — appreciation play?"))));
  out["resNOI"] = J.s(cap_rate_calculator_fmtM(r.num("noi"))+"/yr");out["resVac"] = J.s(cap_rate_calculator_fmtM(r.num("vac")));out["resMgmt"] = J.s(cap_rate_calculator_fmtM(r.num("mgmt")));
  out["resExp"] = J.s(cap_rate_calculator_fmtM(r.num("exp")));out["resMo"] = J.s(cap_rate_calculator_fmtM(r.num("mo"))+"/mo");
  out["shareText"] = J.s("Cap rate "+J.toFixed((r.num("cap")), (2).toInt())+"% (NOI "+cap_rate_calculator_fmtM(r.num("noi"))+")");

    return out
}

private fun cap_rate_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun cap_rate_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+cap_rate_calculator_fmt0(kotlin.math.abs(n));
}

private fun cap_rate_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun cap_rate_calculator_core(p: Inp): Inp {
    var p = p;
  var vac=p.num("rent")*p.num("vacPct")/100; var mgmt=p.num("rent")*p.num("mgmtPct")/100;
  var exp=vac+p.num("tax")+p.num("ins")+p.num("maint")+mgmt;
  var noi=p.num("rent")-exp;
  var cap=(if (p.num("price")>0) noi/p.num("price")*100 else 0);
  return Inp(mapOf("noi" to J.dbl(noi), "cap" to J.dbl(cap), "exp" to J.dbl(exp), "vac" to J.dbl(vac), "mgmt" to J.dbl(mgmt), "mo" to J.dbl(noi/12)));

}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_credit_score_simulator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("score" to J.dbl(inp.num("score")), "onTime" to J.dbl(inp.num("onTime")), "util" to J.dbl(inp.num("util")), "missed" to J.dbl(inp.num("missed")), "newAccts" to J.dbl(inp.num("newAccts")), "histYears" to J.dbl(inp.num("histYears"))))
  var r=credit_score_simulator_core(p);
  out["resScore"] = J.s(J.toStr(r.num("est")));
  
  out["d"] = J.s(((if (r.num("delta")>=0) "▲ +" else "▼ "))+r.num("delta")+" points");
  
  out["resUtil"] = J.s(credit_score_simulator_sgn(r.num("utilAdj")));out["resPay"] = J.s(credit_score_simulator_sgn(r.num("payAdj")));out["resNew"] = J.s(credit_score_simulator_sgn(r.num("newAdj")));out["resHist"] = J.s(credit_score_simulator_sgn(r.num("histAdj")));
  out["resBand"] = J.s(r.num("band"));
  out["shareText"] = J.s("Simulated score "+r.num("est")+" ("+credit_score_simulator_sgn(r.num("delta"))+") from "+p.num("score"));

    return out
}

private fun credit_score_simulator_sgn(n: Number): String {
    var n = n.toDouble();return ((if (n>0) "+" else ""))+n+" pts";
}

private fun credit_score_simulator_core(p: Inp): Inp {
    var p = p;
  var utilAdj=(if (p.num("util")<=10) (10-p.num("util"))*0.8 else -(p.num("util")-10)*1.8);
  var missAdj=(if (p.num("missed") == 0.0) 0 else ((if (p.num("missed") == 1.0) -80 else ((if (p.num("missed") == 2.0) -120 else -150)))));
  var payAdj=-(100-p.num("onTime"))*4+missAdj;
  var newAdj=-p.num("newAccts")*12;
  var histAdj=(if (p.num("histYears")<5) -20 else ((if (p.num("histYears")>15) 10 else 0)));
  var est=p.num("score")+utilAdj+payAdj+newAdj+histAdj;
  est=J.max(300,J.min(850,J.round(est)));
  var band=(if (est>=800) "Exceptional" else ((if (est>=740) "Very Good" else ((if (est>=670) "Good" else ((if (est>=580) "Fair" else "Poor")))))));
  return Inp(mapOf("est" to J.dbl(est), "delta" to J.dbl(est-p.num("score")), "utilAdj" to J.dbl(J.round(utilAdj)), "payAdj" to J.dbl(J.round(payAdj)), "newAdj" to J.dbl(newAdj), "histAdj" to J.dbl(histAdj), "band" to J.dbl(band)));

}

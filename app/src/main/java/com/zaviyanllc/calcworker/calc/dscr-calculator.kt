package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_dscr_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("rent" to J.dbl(inp.num("rent")), "exp" to J.dbl(inp.num("exp")), "loan" to J.dbl(inp.num("loan")), "rate" to J.dbl(inp.num("rate")), "term" to J.dbl(inp.num("term"))))
  var r=dscr_calculator_core(p);
  out["resDSCR"] = J.s(J.toFixed((r.num("dscr")), (2).toInt())+"×");
  
  out["resQual"] = J.s((if (r.num("qual") != 0.0) "✅ Qualifies (≥ 1.20× lender bar)" else "❌ Below 1.20× lender bar"));
  
  out["resNOI"] = J.s(dscr_calculator_fmtM(r.num("noi"))+"/yr");out["resDebt"] = J.s(dscr_calculator_fmtM(r.num("debt"))+"/yr");
  out["resMo"] = J.s(dscr_calculator_fmtM(r.num("mo"))+"/mo");out["resCush"] = J.s(((if (r.num("cush")>=0) "" else "−"))+dscr_calculator_fmtM(kotlin.math.abs(r.num("cush")))+"/yr");
  out["shareText"] = J.s("DSCR "+J.toFixed((r.num("dscr")), (2).toInt())+"× (NOI "+dscr_calculator_fmtM(r.num("noi"))+", debt "+dscr_calculator_fmtM(r.num("debt"))+")");

    return out
}

private fun dscr_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+dscr_calculator_fmt0(kotlin.math.abs(n));
}

private fun dscr_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun dscr_calculator_core(p: Inp): Inp {
    var p = p;
  var noi=p.num("rent")-p.num("exp");
  var debt=p.num("loan")*dscr_calculator_piFactor(p.num("rate"),p.num("term"))*12;
  var dscr=(if (debt>0) noi/debt else 0.0);
  return Inp(mapOf("noi" to J.dbl(noi), "debt" to J.dbl(debt), "dscr" to J.dbl(dscr), "mo" to J.dbl(noi/12), "cush" to J.dbl(noi-debt), "qual" to J.dbl(dscr>=1.2)));

}

private fun dscr_calculator_piFactor(ratePct: Number, years: Number): Double {
    var ratePct = ratePct.toDouble();
    var years = years.toDouble();
  var r=ratePct/100/12; var n=years*12;return (if (r>0) r*J.pw(J.dbl(1+r), J.dbl(n))/(J.pw(J.dbl(1+r), J.dbl(n))-1) else 1/n);

}

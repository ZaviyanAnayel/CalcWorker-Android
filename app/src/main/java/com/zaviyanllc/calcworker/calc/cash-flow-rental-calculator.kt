package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_cash_flow_rental_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("rent" to J.dbl(inp.num("rent")), "pi" to J.dbl(inp.num("pi")), "tax" to J.dbl(inp.num("tax")), "ins" to J.dbl(inp.num("ins")), "vacPct" to J.dbl(inp.num("vacPct")), "maintPct" to J.dbl(inp.num("maintPct")), "mgmtPct" to J.dbl(inp.num("mgmtPct")), "other" to J.dbl(inp.num("other"))))
  var r=cash_flow_rental_calculator_core(p);
  out["resCF"] = J.s(((if (r.num("cf")>=0) "" else "−"))+cash_flow_rental_calculator_fmtM(kotlin.math.abs(r.num("cf"))));
  
  out["s"] = J.s((if (r.num("cf")>=0) "✅ Positive cash flow" else "❌ Negative cash flow"));
  
  out["resEffRent"] = J.s(cash_flow_rental_calculator_fmtM(r.num("effRent")));out["resVac"] = J.s(cash_flow_rental_calculator_fmtM(r.num("vac")));out["resMaint"] = J.s(cash_flow_rental_calculator_fmtM(r.num("maint")));
  out["resMgmt"] = J.s(cash_flow_rental_calculator_fmtM(r.num("mgmt")));out["resExp"] = J.s(cash_flow_rental_calculator_fmtM(r.num("exp")));
  out["resAnnual"] = J.s(((if (r.num("annual")>=0) "" else "−"))+cash_flow_rental_calculator_fmtM(kotlin.math.abs(r.num("annual")))+"/yr");
  out["shareText"] = J.s("Rental cash flow "+cash_flow_rental_calculator_fmtM(r.num("cf"))+"/mo ("+cash_flow_rental_calculator_fmtM(r.num("annual"))+"/yr)");

    return out
}

private fun cash_flow_rental_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+cash_flow_rental_calculator_fmt0(kotlin.math.abs(n));
}

private fun cash_flow_rental_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun cash_flow_rental_calculator_core(p: Inp): Inp {
    var p = p;
  var vac=p.num("rent")*p.num("vacPct")/100; var maint=p.num("rent")*p.num("maintPct")/100; var mgmt=p.num("rent")*p.num("mgmtPct")/100;
  var exp=p.num("pi")+p.num("tax")+p.num("ins")+vac+maint+mgmt+p.num("other");
  var cf=p.num("rent")-exp;
  return Inp(mapOf("cf" to J.dbl(cf), "annual" to J.dbl(cf*12), "exp" to J.dbl(exp), "vac" to J.dbl(vac), "maint" to J.dbl(maint), "mgmt" to J.dbl(mgmt), "effRent" to J.dbl(p.num("rent")-vac)));

}

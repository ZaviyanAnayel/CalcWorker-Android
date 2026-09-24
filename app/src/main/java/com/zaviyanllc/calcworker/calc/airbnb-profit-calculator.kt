package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_airbnb_profit_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("rate" to J.dbl(inp.num("rate")), "nights" to J.dbl(inp.num("nights")), "turnovers" to J.dbl(inp.num("turnovers")), "cleanCost" to J.dbl(inp.num("cleanCost")), "mortgage" to J.dbl(inp.num("mortgage")), "utils" to J.dbl(inp.num("utils")), "supplies" to J.dbl(inp.num("supplies")), "feePct" to J.dbl(inp.num("feePct"))))
  var r=airbnb_profit_calculator_core(p);
  out["resProfit"] = J.s(((if (r.num("profit")>=0) "" else "−"))+airbnb_profit_calculator_fmtM(kotlin.math.abs(r.num("profit"))));
  out["resMargin"] = J.s("Margin "+airbnb_profit_calculator_fmtP(r.num("margin"),1)+" of revenue");
  out["resRev"] = J.s(airbnb_profit_calculator_fmtM(r.num("rev")));out["resOcc"] = J.s(airbnb_profit_calculator_fmtP(r.num("occ"),0));out["resClean"] = J.s(airbnb_profit_calculator_fmtM(r.num("clean")));
  out["resFee"] = J.s(airbnb_profit_calculator_fmtM(r.num("fee")));out["resCost"] = J.s(airbnb_profit_calculator_fmtM(r.num("cost")));
  out["resAnnual"] = J.s(((if (r.num("annual")>=0) "" else "−"))+airbnb_profit_calculator_fmtM(kotlin.math.abs(r.num("annual")))+"/yr");
  out["shareText"] = J.s("Airbnb profit "+airbnb_profit_calculator_fmtM(r.num("profit"))+"/mo at "+J.toFixed((r.num("occ")), (0).toInt())+"% occupancy");

    return out
}

private fun airbnb_profit_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun airbnb_profit_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+airbnb_profit_calculator_fmt0(kotlin.math.abs(n));
}

private fun airbnb_profit_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun airbnb_profit_calculator_core(p: Inp): Inp {
    var p = p;
  var rev=p.num("rate")*p.num("nights");
  var fee=rev*p.num("feePct")/100;
  var clean=p.num("turnovers")*p.num("cleanCost");
  var cost=p.num("mortgage")+p.num("utils")+p.num("supplies")+fee+clean;
  var profit=rev-cost;
  return Inp(mapOf("rev" to J.dbl(rev), "fee" to J.dbl(fee), "clean" to J.dbl(clean), "cost" to J.dbl(cost), "profit" to J.dbl(profit), "annual" to J.dbl(profit*12), "occ" to J.dbl(p.num("nights")/30*100), "margin" to J.dbl((if (rev>0) profit/rev*100 else 0.0))));

}

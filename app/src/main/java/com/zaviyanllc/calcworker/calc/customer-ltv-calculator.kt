package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_customer_ltv_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("arpu" to J.dbl(inp.num("arpu")), "margin" to J.dbl(inp.num("margin")), "churn" to J.dbl(inp.num("churn")), "cac" to J.dbl(inp.num("cac"))))
  var r=customer_ltv_calculator_core(p);
  out["resLTV"] = J.s(customer_ltv_calculator_fmtM(r.num("ltv")));
  out["resLTVSub"] = J.s(r.str("rating"));
  out["resLife"] = J.s(J.toFixed((r.num("life")), (1).toInt())+" months");out["resGP"] = J.s(customer_ltv_calculator_fmtM(r.num("gp"))+"/mo");
  out["resRatio"] = J.s(J.toFixed((r.num("ratio")), (2).toInt())+" : 1");out["resMaxCAC"] = J.s(customer_ltv_calculator_fmtM(r.num("maxCac")));
  out["resProfit"] = J.s(((if (r.num("profit")>=0) "" else "−"))+customer_ltv_calculator_fmtM(kotlin.math.abs(r.num("profit"))));
  out["shareText"] = J.s("LTV "+customer_ltv_calculator_fmtM(r.num("ltv"))+", LTV:CAC "+J.toFixed((r.num("ratio")), (1).toInt())+":1");

    return out
}

private fun customer_ltv_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+customer_ltv_calculator_fmt0(kotlin.math.abs(n));
}

private fun customer_ltv_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun customer_ltv_calculator_core(p: Inp): Inp {
    var p = p;
  var gp=p.num("arpu")*p.num("margin")/100;
  var life=(if (p.num("churn")>0) 1/(p.num("churn")/100) else 0.0);
  var ltv=gp*life;
  var ratio=(if (p.num("cac")>0) ltv/p.num("cac") else 0.0);
  return Inp(mapOf("ltv" to J.dbl(ltv), "life" to J.dbl(life), "gp" to J.dbl(gp), "ratio" to J.dbl(ratio), "maxCac" to J.dbl(ltv/3), "profit" to J.dbl(ltv-p.num("cac")), "rating" to (if (ratio>=3) "✅ Healthy (≥3:1)" else (if (ratio>=1) "⚠️ Tight (<3:1)" else "❌ Unprofitable"))));

}

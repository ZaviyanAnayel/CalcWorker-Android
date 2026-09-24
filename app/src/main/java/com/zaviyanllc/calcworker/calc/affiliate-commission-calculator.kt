package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_affiliate_commission_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("clicks" to J.dbl(inp.num("clicks")), "conv" to J.dbl(inp.num("conv")), "aov" to J.dbl(inp.num("aov")), "comm" to J.dbl(inp.num("comm"))))
  var r=affiliate_commission_calculator_core(p);
  out["resEarn"] = J.s(affiliate_commission_calculator_fmtM(r.num("earn")));
  out["resEarnSub"] = J.s("EPC ${'$'}"+J.toFixed((r.num("epc")), (3).toInt())+" per click");
  out["resSales"] = J.s(affiliate_commission_calculator_fmt0(r.num("sales")));out["resGMV"] = J.s(affiliate_commission_calculator_fmtM(r.num("gmv")));
  out["resEPC"] = J.s("${'$'}"+J.toFixed((r.num("epc")), (3).toInt()));out["resAnnual"] = J.s(affiliate_commission_calculator_fmtM(r.num("annual"))+"/yr");
  out["shareText"] = J.s("Affiliate earnings "+affiliate_commission_calculator_fmtM(r.num("earn"))+"/mo (EPC ${'$'}"+J.toFixed((r.num("epc")), (3).toInt())+")");

    return out
}

private fun affiliate_commission_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun affiliate_commission_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+affiliate_commission_calculator_fmt0(kotlin.math.abs(n));
}

private fun affiliate_commission_calculator_core(p: Inp): Inp {
    var p = p;
  var sales=p.num("clicks")*p.num("conv")/100;
  var gmv=sales*p.num("aov");
  var earn=gmv*p.num("comm")/100;
  return Inp(mapOf("sales" to J.dbl(sales), "gmv" to J.dbl(gmv), "earn" to J.dbl(earn), "epc" to J.dbl((if (p.num("clicks")>0) earn/p.num("clicks") else 0)), "annual" to J.dbl(earn*12)));

}

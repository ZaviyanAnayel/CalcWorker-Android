package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_stripe_fee_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("amount" to J.dbl(inp.num("amount")), "rate" to J.dbl(inp.num("rate")), "fixed" to J.dbl(inp.num("fixed"))))
  var r=stripe_fee_calculator_core(p);
  out["resFee"] = J.s(stripe_fee_calculator_fmtM(r.num("fee")));
  out["resNet"] = J.s("You keep "+stripe_fee_calculator_fmtM(r.num("net")));
  out["resReceive"] = J.s(stripe_fee_calculator_fmtM(r.num("net")));out["resEff"] = J.s(stripe_fee_calculator_fmtP(r.num("eff"),2));
  out["resK"] = J.s(stripe_fee_calculator_fmtM(r.num("k")));out["resMo"] = J.s(stripe_fee_calculator_fmtM(r.num("mo"))+"/mo");out["resGrossUp"] = J.s(stripe_fee_calculator_fmtM(r.num("grossUp")));
  out["shareText"] = J.s("Stripe fee "+stripe_fee_calculator_fmtM(r.num("fee"))+" on "+stripe_fee_calculator_fmtM(p.num("amount"))+" (net "+stripe_fee_calculator_fmtM(r.num("net"))+")");

    return out
}

private fun stripe_fee_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun stripe_fee_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+stripe_fee_calculator_fmt0(kotlin.math.abs(n));
}

private fun stripe_fee_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun stripe_fee_calculator_core(p: Inp): Inp {
    var p = p;
  var fee=p.num("amount")*p.num("rate")/100+p.num("fixed");
  var net=p.num("amount")-fee;
  var grossUp=(if (p.num("rate")<100) (100+p.num("fixed"))/(1-p.num("rate")/100) else 0);
  return Inp(mapOf("fee" to J.dbl(fee), "net" to J.dbl(net), "eff" to J.dbl((if (p.num("amount")>0) fee/p.num("amount")*100 else 0)), "k" to J.dbl((if (p.num("amount")>0) (1000/p.num("amount"))*fee else 0)), "mo" to J.dbl((if (p.num("amount")>0) (10000/p.num("amount"))*fee else 0)), "grossUp" to J.dbl(grossUp)));

}

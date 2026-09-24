package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_tiktok_shop_profit_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("units" to J.dbl(inp.num("units")), "price" to J.dbl(inp.num("price")), "commPct" to J.dbl(inp.num("commPct")), "cost" to J.dbl(inp.num("cost")), "ship" to J.dbl(inp.num("ship")), "feePct" to J.dbl(inp.num("feePct")), "ads" to J.dbl(inp.num("ads"))))
  var r=tiktok_shop_profit_calculator_core(p);
  out["resProfit"] = J.s(((if (r.num("profit")>=0) "" else "−"))+tiktok_shop_profit_calculator_fmtM(kotlin.math.abs(r.num("profit"))));
  out["resMargin"] = J.s("Net margin "+tiktok_shop_profit_calculator_fmtP(r.num("margin"),1));
  out["resRev"] = J.s(tiktok_shop_profit_calculator_fmtM(r.num("rev")));out["resComm"] = J.s(tiktok_shop_profit_calculator_fmtM(r.num("comm")));out["resCogs"] = J.s(tiktok_shop_profit_calculator_fmtM(r.num("cogs")));
  out["resFee"] = J.s(tiktok_shop_profit_calculator_fmtM(r.num("fee")));out["resAds"] = J.s(tiktok_shop_profit_calculator_fmtM(r.num("ads")));out["resPerUnit"] = J.s(tiktok_shop_profit_calculator_fmtM(r.num("perUnit"))+"/unit");
  out["shareText"] = J.s("TikTok Shop profit "+tiktok_shop_profit_calculator_fmtM(r.num("profit"))+"/mo ("+J.toFixed((r.num("margin")), (1).toInt())+"% margin)");

    return out
}

private fun tiktok_shop_profit_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun tiktok_shop_profit_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+tiktok_shop_profit_calculator_fmt0(kotlin.math.abs(n));
}

private fun tiktok_shop_profit_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun tiktok_shop_profit_calculator_core(p: Inp): Inp {
    var p = p;
  var rev=p.num("units")*p.num("price");
  var comm=rev*p.num("commPct")/100;
  var cogs=p.num("units")*(p.num("cost")+p.num("ship"));
  var fee=rev*p.num("feePct")/100;
  var profit=rev-comm-cogs-fee-p.num("ads");
  return Inp(mapOf("rev" to J.dbl(rev), "comm" to J.dbl(comm), "cogs" to J.dbl(cogs), "fee" to J.dbl(fee), "profit" to J.dbl(profit), "perUnit" to J.dbl((if (p.num("units")>0) profit/p.num("units") else 0)), "margin" to J.dbl((if (rev>0) profit/rev*100 else 0.0))));

}

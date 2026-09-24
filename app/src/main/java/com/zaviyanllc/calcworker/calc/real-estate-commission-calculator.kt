package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_real_estate_commission_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("price" to J.dbl(inp.num("price")), "commPct" to J.dbl(inp.num("commPct")), "listSplit" to J.dbl(inp.num("listSplit")), "agentSplit" to J.dbl(inp.num("agentSplit"))))
  var r=real_estate_commission_calculator_core(p);
  out["resNet"] = J.s(real_estate_commission_calculator_fmtM(r.num("net")));
  out["resNetSub"] = J.s("After "+real_estate_commission_calculator_fmtM(r.num("total"))+" commission ("+real_estate_commission_calculator_fmtP(p.num("commPct"),1)+")");
  out["resTotal"] = J.s(real_estate_commission_calculator_fmtM(r.num("total")));out["resList"] = J.s(real_estate_commission_calculator_fmtM(r.num("list")));out["resBuy"] = J.s(real_estate_commission_calculator_fmtM(r.num("buy")));
  out["resAgent"] = J.s(real_estate_commission_calculator_fmtM(r.num("agent")));out["resBroker"] = J.s(real_estate_commission_calculator_fmtM(r.num("broker")));
  out["shareText"] = J.s("Commission "+real_estate_commission_calculator_fmtM(r.num("total"))+" on "+real_estate_commission_calculator_fmtM(p.num("price"))+", seller nets "+real_estate_commission_calculator_fmtM(r.num("net")));

    return out
}

private fun real_estate_commission_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun real_estate_commission_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+real_estate_commission_calculator_fmt0(kotlin.math.abs(n));
}

private fun real_estate_commission_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun real_estate_commission_calculator_core(p: Inp): Inp {
    var p = p;
  var total=p.num("price")*p.num("commPct")/100;
  var list=total*p.num("listSplit")/100;
  var buy=total-list;
  var agent=list*p.num("agentSplit")/100;
  var broker=list-agent;
  return Inp(mapOf("total" to J.dbl(total), "list" to J.dbl(list), "buy" to J.dbl(buy), "agent" to J.dbl(agent), "broker" to J.dbl(broker), "net" to J.dbl(p.num("price")-total)));

}

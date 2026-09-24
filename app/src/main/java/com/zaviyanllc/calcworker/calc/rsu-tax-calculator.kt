package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_rsu_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("shares" to J.dbl(inp.num("shares")), "price" to J.dbl(inp.num("price")), "whRate" to J.dbl(inp.num("whRate")), "stateRate" to J.dbl(inp.num("stateRate"))))
  var r=rsu_tax_calculator_core(p);
  out["resNet"] = J.s(rsu_tax_calculator_fmtM(r.num("net")));
  out["resKeep"] = J.s("You keep "+rsu_tax_calculator_fmtP(r.num("keep"),1)+" of vest value");
  out["resGross"] = J.s(rsu_tax_calculator_fmtM(r.num("gross")));out["resFed"] = J.s(rsu_tax_calculator_fmtM(r.num("fed")));out["resSS"] = J.s(rsu_tax_calculator_fmtM(r.num("ss")));
  out["resMed"] = J.s(rsu_tax_calculator_fmtM(r.num("med")));out["resState"] = J.s(rsu_tax_calculator_fmtM(r.num("state")));
  out["resBasis"] = J.s(rsu_tax_calculator_fmtM(r.num("basis")));
  out["shareText"] = J.s("RSU vest "+rsu_tax_calculator_fmtM(r.num("gross"))+": after-tax "+rsu_tax_calculator_fmtM(r.num("net")));

    return out
}

private fun rsu_tax_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun rsu_tax_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+rsu_tax_calculator_fmt0(kotlin.math.abs(n));
}

private fun rsu_tax_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun rsu_tax_calculator_core(p: Inp): Inp {
    var p = p;
  var gross=p.num("shares")*p.num("price");
  var fed=gross*p.num("whRate")/100;
  var ss=gross*0.062;
  var med=gross*0.0145;
  var state=gross*p.num("stateRate")/100;
  var tot=fed+ss+med+state;
  return Inp(mapOf("gross" to J.dbl(gross), "fed" to J.dbl(fed), "ss" to J.dbl(ss), "med" to J.dbl(med), "state" to J.dbl(state), "tot" to J.dbl(tot), "net" to J.dbl(gross-tot), "basis" to J.dbl(p.num("price")), "keep" to J.dbl((if (gross>0) (gross-tot)/gross*100 else 0.0))));

}

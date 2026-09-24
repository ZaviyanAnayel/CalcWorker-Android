package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_bonus_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("bonus" to J.dbl(inp.num("bonus")), "stateRate" to J.dbl(inp.num("stateRate"))))
  var r=bonus_tax_calculator_core(p);
  out["resNet"] = J.s(bonus_tax_calculator_fmtM(r.num("net")));
  out["resKeep"] = J.s("You keep "+bonus_tax_calculator_fmtP(r.num("keep"),1)+" of the bonus");
  out["resFed"] = J.s(bonus_tax_calculator_fmtM(r.num("fed")));out["resSS"] = J.s(bonus_tax_calculator_fmtM(r.num("ss")));out["resMed"] = J.s(bonus_tax_calculator_fmtM(r.num("med")));
  out["resState"] = J.s(bonus_tax_calculator_fmtM(r.num("state")));out["resTot"] = J.s(bonus_tax_calculator_fmtM(r.num("tot")));
  out["shareText"] = J.s("Net bonus on "+bonus_tax_calculator_fmtM(p.num("bonus"))+": "+bonus_tax_calculator_fmtM(r.num("net")));

    return out
}

private fun bonus_tax_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun bonus_tax_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+bonus_tax_calculator_fmt0(kotlin.math.abs(n));
}

private fun bonus_tax_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun bonus_tax_calculator_core(p: Inp): Inp {
    var p = p;
  var fed=p.num("bonus")*0.22;
  var ss=p.num("bonus")*0.062;
  var med=p.num("bonus")*0.0145;
  var state=p.num("bonus")*p.num("stateRate")/100;
  var tot=fed+ss+med+state;
  return Inp(mapOf("fed" to J.dbl(fed), "ss" to J.dbl(ss), "med" to J.dbl(med), "state" to J.dbl(state), "tot" to J.dbl(tot), "net" to J.dbl(p.num("bonus")-tot), "keep" to J.dbl((if (p.num("bonus")>0) (p.num("bonus")-tot)/p.num("bonus")*100 else 0))));

}

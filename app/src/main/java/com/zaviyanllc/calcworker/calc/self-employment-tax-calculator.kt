package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_self_employment_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var r=self_employment_tax_calculator_core(Inp(mapOf("net" to J.dbl(inp.num("net")))));
  out["resSE"] = J.s(self_employment_tax_calculator_fmtM(r.num("total")));
  out["resEff"] = J.s("Effective "+self_employment_tax_calculator_fmtP(r.num("eff"),1)+"% of net earnings");
  out["resSS"] = J.s(self_employment_tax_calculator_fmtM(r.num("ss")));out["resMed"] = J.s(self_employment_tax_calculator_fmtM(r.num("med")));out["resBase"] = J.s(self_employment_tax_calculator_fmtM(r.num("base")));out["resHalf"] = J.s(self_employment_tax_calculator_fmtM(r.num("half")));
  out["shareText"] = J.s("SE tax on "+self_employment_tax_calculator_fmtM(inp.num("net"))+": "+self_employment_tax_calculator_fmtM(r.num("total")));

    return out
}

private fun self_employment_tax_calculator_core(p: Inp): Inp {
    var p = p;
  var se=self_employment_tax_calculator_seTaxF(p.num("net"));
  return Inp(mapOf("total" to J.dbl(se.num("total")), "ss" to J.dbl(se.num("ss")), "med" to J.dbl(se.num("med")), "base" to J.dbl(se.num("base")), "half" to J.dbl(se.num("total")/2), "eff" to J.dbl((if (p.num("net")>0) se.num("total")/p.num("net")*100 else 0))));

}

private fun self_employment_tax_calculator_seTaxF(net: Number): Inp {
    var net = net.toDouble();
  var base=net*0.9235;
  var ss=J.min(base,176100)*0.062*2; 
  var med=base*0.029;
  return Inp(mapOf("total" to J.dbl(ss+med), "base" to J.dbl(base), "ss" to J.dbl(ss), "med" to J.dbl(med)));

}

private fun self_employment_tax_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun self_employment_tax_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+self_employment_tax_calculator_fmt0(kotlin.math.abs(n));
}

private fun self_employment_tax_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

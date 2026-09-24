package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_quarterly_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("profit" to J.dbl(inp.num("profit")), "fstatus" to J.dbl(J.piD(inp.str("fstatus"))), "priorTax" to J.dbl(inp.num("priorTax")), "highAGI" to J.dbl(J.piD(inp.str("highAGI"))), "withheld" to J.dbl(inp.num("withheld"))))
  var r=quarterly_tax_calculator_core(p);
  out["resQ"] = J.s(quarterly_tax_calculator_fmtM(r.num("q")));
  out["resTotal"] = J.s(quarterly_tax_calculator_fmtM(r.num("total")));out["resSafe"] = J.s(quarterly_tax_calculator_fmtM(r.num("safe")));
  out["resReq"] = J.s(quarterly_tax_calculator_fmtM(r.num("req")));out["resRem"] = J.s(quarterly_tax_calculator_fmtM(r.num("rem")));
  out["resDue"] = J.s("Due Apr 15 \u2022 Jun 15 \u2022 Sep 15 \u2022 Jan 15");
  out["shareText"] = J.s("Quarterly estimated payment "+quarterly_tax_calculator_fmtM(r.num("q"))+" (safe harbor "+quarterly_tax_calculator_fmtM(r.num("safe"))+")");

    return out
}

private fun quarterly_tax_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+quarterly_tax_calculator_fmt0(kotlin.math.abs(n));
}

private fun quarterly_tax_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun quarterly_tax_calculator_core(p: Inp): Inp {
    var p = p;
  var married=p.num("fstatus") == 1.0;
  var se=quarterly_tax_calculator_seTaxF(p.num("profit")).num("total");
  var taxable=J.max(0,p.num("profit")-se/2-((if (J.truthy(married)) 30000 else 15000)));
  var fed=quarterly_tax_calculator_fedTax(taxable, if (married) 1.0 else 0.0);
  var total=se+fed;
  var safe=p.num("priorTax")*((if (p.num("highAGI") == 1.0) 1.10 else 1.00));
  var req=J.min(total*0.90,safe);
  var rem=J.max(0,req-p.num("withheld"));
  return Inp(mapOf("total" to J.dbl(total), "safe" to J.dbl(safe), "req" to J.dbl(req), "rem" to J.dbl(rem), "q" to J.dbl(rem/4), "se" to J.dbl(se), "fed" to J.dbl(fed)));

}

private fun quarterly_tax_calculator_seTaxF(net: Number): Inp {
    var net = net.toDouble();
  var base=net*0.9235;
  var ss=J.min(base,176100)*0.062*2; 
  var med=base*0.029;
  return Inp(mapOf("total" to J.dbl(ss+med), "base" to J.dbl(base), "ss" to J.dbl(ss), "med" to J.dbl(med)));

}

private fun quarterly_tax_calculator_fedTax(taxable: Number, married: Number): Double {
    var taxable = taxable.toDouble();
    var married = married.toDouble();
  var mult=(if (J.truthy(married)) 2 else 1);
  var br=listOf(listOf(11925.0,0.10),listOf(48475.0,0.12),listOf(103350.0,0.22),listOf(197300.0,0.24),listOf(250525.0,0.32),listOf(626350.0,0.35),listOf(1e15,0.37));
  var tax = 0.0; var prev = 0.0;
  var i: Int = 0; while (i<br.size) {var cap=br[i][0]*mult; var rate=br[i][1];
    if(taxable>prev){tax+= (J.min(taxable,cap)-prev)*rate;prev=cap;}else break; i += 1}
  return tax;

}

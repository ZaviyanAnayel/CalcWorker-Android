package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_llc_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("profit" to J.dbl(inp.num("profit")), "fstatus" to J.dbl(J.piD(inp.str("fstatus"))), "stateRate" to J.dbl(inp.num("stateRate"))))
  var r=llc_tax_calculator_core(p);
  out["resTotal"] = J.s(llc_tax_calculator_fmtM(r.num("total")));
  out["resEff"] = J.s("Effective rate "+llc_tax_calculator_fmtP(r.num("eff"),1)+" of profit");
  out["resSE"] = J.s(llc_tax_calculator_fmtM(r.num("se")));out["resFed"] = J.s(llc_tax_calculator_fmtM(r.num("fed")));out["resState"] = J.s(llc_tax_calculator_fmtM(r.num("state")));out["resNet"] = J.s(llc_tax_calculator_fmtM(r.num("net")));
  out["shareText"] = J.s("LLC tax on "+llc_tax_calculator_fmtM(p.num("profit"))+": "+llc_tax_calculator_fmtM(r.num("total"))+" total ("+J.toFixed((r.num("eff")), (1).toInt())+"% effective)");

    return out
}

private fun llc_tax_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun llc_tax_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+llc_tax_calculator_fmt0(kotlin.math.abs(n));
}

private fun llc_tax_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun llc_tax_calculator_core(p: Inp): Inp {
    var p = p;
  var married=p.num("fstatus") == 1.0;
  var se=llc_tax_calculator_seTaxF(p.num("profit"));
  var taxable=J.max(0,p.num("profit")-se.num("total")/2-((if (J.truthy(married)) 30000 else 15000)));
  var fed=llc_tax_calculator_fedTax(taxable, if (married) 1.0 else 0.0);
  var state=p.num("profit")*p.num("stateRate")/100;
  var total=se.num("total")+fed+state;
  return Inp(mapOf("se" to J.dbl(se.num("total")), "fed" to J.dbl(fed), "state" to J.dbl(state), "total" to J.dbl(total), "net" to J.dbl(p.num("profit")-total), "eff" to J.dbl((if (p.num("profit")>0) total/p.num("profit")*100 else 0))));

}

private fun llc_tax_calculator_seTaxF(net: Number): Inp {
    var net = net.toDouble();
  var base=net*0.9235;
  var ss=J.min(base,176100)*0.062*2; 
  var med=base*0.029;
  return Inp(mapOf("total" to J.dbl(ss+med), "base" to J.dbl(base), "ss" to J.dbl(ss), "med" to J.dbl(med)));

}

private fun llc_tax_calculator_fedTax(taxable: Number, married: Number): Double {
    var taxable = taxable.toDouble();
    var married = married.toDouble();
  var mult=(if (J.truthy(married)) 2 else 1);
  var br=listOf(listOf(11925.0,0.10),listOf(48475.0,0.12),listOf(103350.0,0.22),listOf(197300.0,0.24),listOf(250525.0,0.32),listOf(626350.0,0.35),listOf(1e15,0.37));
  var tax = 0.0; var prev = 0.0;
  var i: Int = 0; while (i<br.size) {var cap=br[i][0]*mult; var rate=br[i][1];
    if(taxable>prev){tax+= (J.min(taxable,cap)-prev)*rate;prev=cap;}else break; i += 1}
  return tax;

}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_tax_refund_estimator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("salary" to J.dbl(inp.num("salary")), "fstatus" to J.dbl(J.piD(inp.str("fstatus"))), "pretax" to J.dbl(inp.num("pretax")), "withheld" to J.dbl(inp.num("withheld")), "credits" to J.dbl(inp.num("credits"))))
  var r=tax_refund_estimator_core(p);
  out["resOut"] = J.s(tax_refund_estimator_fmtM(kotlin.math.abs(r.num("diff"))));
  
  out["resOutSub"] = J.s((if (r.num("refund") != 0.0) "🎉 Refund coming your way" else "⚠️ Balance due at filing"));
  
  out["resTax"] = J.s(tax_refund_estimator_fmtM(r.num("tax")));out["resPaid"] = J.s(tax_refund_estimator_fmtM(p.num("withheld")));out["resCred"] = J.s(tax_refund_estimator_fmtM(p.num("credits")));
  out["resTaxable"] = J.s(tax_refund_estimator_fmtM(r.num("taxable")));out["resEff"] = J.s(tax_refund_estimator_fmtP(r.num("eff"),1));
  out["shareText"] = J.s("Projected "+((if (r.num("refund") != 0.0) "refund " else "balance due "))+tax_refund_estimator_fmtM(kotlin.math.abs(r.num("diff"))));

    return out
}

private fun tax_refund_estimator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun tax_refund_estimator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+tax_refund_estimator_fmt0(kotlin.math.abs(n));
}

private fun tax_refund_estimator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun tax_refund_estimator_core(p: Inp): Inp {
    var p = p;
  var married=p.num("fstatus") == 1.0;
  var taxable=J.max(0,p.num("salary")-p.num("pretax")-((if (J.truthy(married)) 30000 else 15000)));
  var tax=J.max(0,tax_refund_estimator_fedTax(taxable, if (married) 1.0 else 0.0)-p.num("credits"));
  var diff=p.num("withheld")-tax;
  return Inp(mapOf("tax" to J.dbl(tax), "diff" to J.dbl(diff), "taxable" to J.dbl(taxable), "refund" to J.dbl(diff>=0), "eff" to J.dbl((if (p.num("salary")>0) tax/p.num("salary")*100 else 0))));

}

private fun tax_refund_estimator_fedTax(taxable: Number, married: Number): Double {
    var taxable = taxable.toDouble();
    var married = married.toDouble();
  var mult=(if (J.truthy(married)) 2 else 1);
  var br=listOf(listOf(11925.0,0.10),listOf(48475.0,0.12),listOf(103350.0,0.22),listOf(197300.0,0.24),listOf(250525.0,0.32),listOf(626350.0,0.35),listOf(1e15,0.37));
  var tax = 0.0; var prev = 0.0;
  var i: Int = 0; while (i<br.size) {var cap=br[i][0]*mult; var rate=br[i][1];
    if(taxable>prev){tax+= (J.min(taxable,cap)-prev)*rate;prev=cap;}else break; i += 1}
  return tax;

}

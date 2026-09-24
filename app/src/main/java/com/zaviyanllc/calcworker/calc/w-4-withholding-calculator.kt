package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_w_4_withholding_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("salary" to J.dbl(inp.num("salary")), "fstatus" to J.dbl(J.piD(inp.str("fstatus"))), "pretax" to J.dbl(inp.num("pretax")), "periods" to J.dbl(inp.num("periods")), "curwh" to J.dbl(inp.num("curwh"))))
  var r=w_4_withholding_calculator_core(p);
  
  out["resDiff"] = J.s(((if (r.num("diff")>=0) "+" else "−"))+"${'$'}"+w_4_withholding_calculator_fmt0(kotlin.math.abs(r.num("diff")))+"/check");
  
  out["resDiffSub"] = J.s((if (r.num("diff")>=0) "Over-withheld (refund coming)" else "Under-withheld (bill coming)"));
  
  out["resTax"] = J.s(w_4_withholding_calculator_fmtM(r.num("tax")));out["resPer"] = J.s(w_4_withholding_calculator_fmtM(r.num("per"))+"/check");
  out["resTaxable"] = J.s(w_4_withholding_calculator_fmtM(r.num("taxable")));out["resBracket"] = J.s(r.str("bracket"));
  out["resRefund"] = J.s(((if (r.num("refund")>=0) "Refund " else "Owed "))+w_4_withholding_calculator_fmtM(kotlin.math.abs(r.num("refund"))));
  out["shareText"] = J.s("Est. federal tax "+w_4_withholding_calculator_fmtM(r.num("tax"))+", correct withholding "+w_4_withholding_calculator_fmtM(r.num("per"))+"/paycheck");

    return out
}

private fun w_4_withholding_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun w_4_withholding_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+w_4_withholding_calculator_fmt0(kotlin.math.abs(n));
}

private fun w_4_withholding_calculator_core(p: Inp): Inp {
    var p = p;
  var married=p.num("fstatus") == 1.0;
  var stdDed=(if (J.truthy(married)) 30000 else 15000);
  var taxable=J.max(0,p.num("salary")-p.num("pretax")-stdDed);
  var tax=w_4_withholding_calculator_fedTax(taxable, if (married) 1.0 else 0.0);
  var per=(if (p.num("periods")>0) tax/p.num("periods") else 0.0);
  var diff=p.num("curwh")-per;
  var refund=diff*p.num("periods");
  var br=(if (taxable<=11925*((if (J.truthy(married)) 2 else 1))) "10%" else ((if (taxable<=48475*((if (J.truthy(married)) 2 else 1))) "12%" else ((if (taxable<=103350*((if (J.truthy(married)) 2 else 1))) "22%" else ((if (taxable<=197300*((if (J.truthy(married)) 2 else 1))) "24%" else "32%+")))))));
  return Inp(mapOf("tax" to J.dbl(tax), "per" to J.dbl(per), "diff" to J.dbl(diff), "refund" to J.dbl(refund), "taxable" to J.dbl(taxable), "bracket" to br));

}

private fun w_4_withholding_calculator_fedTax(taxable: Number, married: Number): Double {
    var taxable = taxable.toDouble();
    var married = married.toDouble();
  var mult=(if (J.truthy(married)) 2 else 1);
  var br=listOf(listOf(11925.0,0.10),listOf(48475.0,0.12),listOf(103350.0,0.22),listOf(197300.0,0.24),listOf(250525.0,0.32),listOf(626350.0,0.35),listOf(1e15,0.37));
  var tax = 0.0; var prev = 0.0;
  var i: Int = 0; while (i<br.size) {var cap=br[i][0]*mult; var rate=br[i][1];
    if(taxable>prev){tax+= (J.min(taxable,cap)-prev)*rate;prev=cap;}else break; i += 1}
  return tax;

}

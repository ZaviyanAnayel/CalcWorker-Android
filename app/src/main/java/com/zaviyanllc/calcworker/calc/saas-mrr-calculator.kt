package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_saas_mrr_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("cust" to J.dbl(inp.num("cust")), "arpu" to J.dbl(inp.num("arpu")), "expPct" to J.dbl(inp.num("expPct")), "churnPct" to J.dbl(inp.num("churnPct"))))
  var r=saas_mrr_calculator_core(p);
  out["resMRR"] = J.s(saas_mrr_calculator_fmtM(r.num("mrr"))+"/mo");
  out["resMRRSub"] = J.s("Net "+((if (r.num("net")>=0) "+" else "−"))+saas_mrr_calculator_fmtM(kotlin.math.abs(r.num("net")))+"/mo");
  out["resARR"] = J.s(saas_mrr_calculator_fmtM(r.num("arr"))+"/yr");out["resExp"] = J.s("+"+saas_mrr_calculator_fmtM(r.num("exp")));
  out["resChurn"] = J.s("−"+saas_mrr_calculator_fmtM(r.num("churn")));out["resNext"] = J.s(saas_mrr_calculator_fmtM(r.num("next"))+"/mo");
  out["resNRR"] = J.s(saas_mrr_calculator_fmtP(r.num("nrr"),1));
  out["shareText"] = J.s("MRR "+saas_mrr_calculator_fmtM(r.num("mrr"))+"/mo, NRR "+J.toFixed((r.num("nrr")), (1).toInt())+"%");

    return out
}

private fun saas_mrr_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun saas_mrr_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+saas_mrr_calculator_fmt0(kotlin.math.abs(n));
}

private fun saas_mrr_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun saas_mrr_calculator_core(p: Inp): Inp {
    var p = p;
  var mrr=p.num("cust")*p.num("arpu");
  var exp=mrr*p.num("expPct")/100; var churn=mrr*p.num("churnPct")/100;
  var next=mrr+exp-churn;
  return Inp(mapOf("mrr" to J.dbl(mrr), "arr" to J.dbl(mrr*12), "exp" to J.dbl(exp), "churn" to J.dbl(churn), "next" to J.dbl(next), "nrr" to J.dbl((if (mrr>0) next/mrr*100 else 0.0)), "net" to J.dbl(exp-churn)));

}

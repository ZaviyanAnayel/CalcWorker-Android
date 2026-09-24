package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_saas_churn_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("start" to J.dbl(inp.num("start")), "lost" to J.dbl(inp.num("lost")), "new" to J.dbl(inp.num("new")), "mrrStart" to J.dbl(inp.num("mrrStart")), "mrrLost" to J.dbl(inp.num("mrrLost"))))
  var r=saas_churn_calculator_core(p);
  out["resChurn"] = J.s(saas_churn_calculator_fmtP(r.num("logo"),2));
  out["resChurnSub"] = J.s(r.str("rating"));
  out["resRev"] = J.s(saas_churn_calculator_fmtP(r.num("rev"),2));out["resRet"] = J.s(saas_churn_calculator_fmtP(r.num("ret"),2));
  out["resEnd"] = J.s(saas_churn_calculator_fmt0(r.num("end")));
  out["resNet"] = J.s(((if (r.num("net")>=0) "+" else "−"))+saas_churn_calculator_fmt0(kotlin.math.abs(r.num("net"))));
  out["resBE"] = J.s(saas_churn_calculator_fmt0(r.num("be"))+" customers");
  out["shareText"] = J.s("Churn "+J.toFixed((r.num("logo")), (2).toInt())+"% logo, "+J.toFixed((r.num("rev")), (2).toInt())+"% revenue");

    return out
}

private fun saas_churn_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun saas_churn_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun saas_churn_calculator_core(p: Inp): Inp {
    var p = p;
  var logo=(if (p.num("start")>0) p.num("lost")/p.num("start")*100 else 0.0);
  var rev=(if (p.num("mrrStart")>0) p.num("mrrLost")/p.num("mrrStart")*100 else 0.0);
  var end=p.num("start")-p.num("lost")+p.num("new");
  return Inp(mapOf("logo" to J.dbl(logo), "rev" to J.dbl(rev), "ret" to J.dbl(100-logo), "end" to J.dbl(end), "net" to J.dbl(p.num("new")-p.num("lost")), "be" to J.dbl(p.num("lost")), "rating" to (if (logo<2) "✅ Excellent" else ((if (logo<=5) "⚠️ Watch it" else "❌ Critical")))));

}

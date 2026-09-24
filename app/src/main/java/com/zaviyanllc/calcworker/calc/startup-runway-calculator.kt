package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_startup_runway_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("cash" to J.dbl(inp.num("cash")), "rev" to J.dbl(inp.num("rev")), "exp" to J.dbl(inp.num("exp"))))
  var r=startup_runway_calculator_core(p);
  out["resRun"] = J.s(if (J.isFin(r.num("run"))) J.toFixed(r.num("run"), 1) + " months" else "∞");
  out["resZero"] = J.s(if (r.num("burn")>0) "Out of cash ≈ " + r.str("zero") else "Cash growing monthly");
  out["resBurn"] = J.s(((if (r.num("burn")>=0) "" else "−"))+startup_runway_calculator_fmtM(kotlin.math.abs(r.num("burn")))+"/mo");
  out["resGross"] = J.s(startup_runway_calculator_fmtM(r.num("gross"))+"/mo");
  out["resMult"] = J.s("—");
  out["resNeed"] = J.s(startup_runway_calculator_fmtM(r.num("need")));
  out["resStatus"] = J.s(r.str("status"));
  out["shareText"] = J.s("Runway " + (if (J.isFin(r.num("run"))) J.toFixed(r.num("run"), 1) + " months" else "infinite") + ", burn " + startup_runway_calculator_fmtM(r.num("burn")) + "/mo");

    return out
}

private fun startup_runway_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+startup_runway_calculator_fmt0(kotlin.math.abs(n));
}

private fun startup_runway_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun startup_runway_calculator_core(p: Inp): Inp {
    var p = p;
  var burn=p.num("exp")-p.num("rev");
  var run=(if (burn>0) p.num("cash")/burn else Double.POSITIVE_INFINITY);
  var months=listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
  val zeroStr = if (burn>0 && J.isFin(run)) {
    val zd = java.time.LocalDate.now().plusMonths(kotlin.math.floor(run).toLong());
    months[zd.monthValue - 1] + " " + zd.year
  } else "—";
  return Inp(mapOf("burn" to J.dbl(burn), "run" to J.dbl(run), "gross" to J.dbl(p.num("exp")), "need" to J.dbl((if (burn>0) burn*18 else 0.0)), "zero" to zeroStr, "status" to (if (burn<=0) "✅ Profitable — infinite runway" else ((if (run<6) "🔴 Danger (<6 mo)" else ((if (run<12) "🟡 Raise soon (<12 mo)" else "🟢 Healthy (12+ mo)")))))));

}

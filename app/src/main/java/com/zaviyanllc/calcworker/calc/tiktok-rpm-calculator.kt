package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_tiktok_rpm_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("views" to J.dbl(inp.num("views")), "revenue" to J.dbl(inp.num("revenue")), "days" to J.dbl(inp.num("days")), "target" to J.dbl(inp.num("target"))))
  var r=tiktok_rpm_calculator_core(p);
  out["resRPM"] = J.s("${'$'}"+J.toFixed((r.num("rpm")), (2).toInt()));
  out["resTarget"] = J.s(tiktok_rpm_calculator_fmtM(r.num("target")));
  out["resDaily"] = J.s(tiktok_rpm_calculator_fmt0(r.num("daily")));
  out["resMonthly"] = J.s(tiktok_rpm_calculator_fmtM(r.num("monthly"))+"/mo");
  out["resPerM"] = J.s(tiktok_rpm_calculator_fmtM(r.num("perM")));
  out["resRPMSub"] = J.s("per 1,000 qualified views");
  out["shareText"] = J.s("TikTok RPM ${'$'}"+J.toFixed((r.num("rpm")), (2).toInt())+", target views project "+tiktok_rpm_calculator_fmtM(r.num("target")));

    return out
}

private fun tiktok_rpm_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun tiktok_rpm_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+tiktok_rpm_calculator_fmt0(kotlin.math.abs(n));
}

private fun tiktok_rpm_calculator_core(p: Inp): Inp {
    var p = p;
  var rpm=(if (p.num("views")>0) p.num("revenue")/p.num("views")*1000 else 0.0);
  var daily=(if (p.num("days")>0) p.num("views")/p.num("days") else 0.0);
  var monthly=daily*30/1000*rpm;
  return Inp(mapOf("rpm" to J.dbl(rpm), "target" to J.dbl(p.num("target")/1000*rpm), "daily" to J.dbl(daily), "monthly" to J.dbl(monthly), "perM" to J.dbl(rpm*1000)));

}

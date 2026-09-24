package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_instagram_engagement_rate_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("followers" to J.dbl(inp.num("followers")), "likes" to J.dbl(inp.num("likes")), "comments" to J.dbl(inp.num("comments")), "shares" to J.dbl(inp.num("shares")), "saves" to J.dbl(inp.num("saves"))))
  var r=instagram_engagement_rate_calculator_core(p);
  out["resER"] = J.s(instagram_engagement_rate_calculator_fmtP(r.num("er"),2));
  out["resERSub"] = J.s(r.str("rating"));
  out["resTrue"] = J.s(instagram_engagement_rate_calculator_fmtP(r.num("tru"),2));out["resInter"] = J.s(instagram_engagement_rate_calculator_fmt0(r.num("inter")));out["resBench"] = J.s(r.str("bench"));
  out["shareText"] = J.s("Instagram ER "+J.toFixed((r.num("er")), (2).toInt())+"% ("+instagram_engagement_rate_calculator_fmt0(p.num("followers"))+" followers)");

    return out
}

private fun instagram_engagement_rate_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun instagram_engagement_rate_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun instagram_engagement_rate_calculator_core(p: Inp): Inp {
    var p = p;
  var er=(if (p.num("followers")>0) (p.num("likes")+p.num("comments"))/p.num("followers")*100 else 0.0);
  var tru=(if (p.num("followers")>0) (p.num("likes")+p.num("comments")+p.num("shares")+p.num("saves"))/p.num("followers")*100 else 0.0);
  var bench=(if (p.num("followers")<10000) "3–5% (nano)" else ((if (p.num("followers")<100000) "2–4% (micro)" else ((if (p.num("followers")<1000000) "1.5–3% (mid-tier)" else "1–2% (macro)")))));
  return Inp(mapOf("er" to J.dbl(er), "tru" to J.dbl(tru), "inter" to J.dbl(p.num("likes")+p.num("comments")+p.num("shares")+p.num("saves")), "bench" to bench, "rating" to (if (er>=4) "🔥 Excellent" else ((if (er>=2) "✅ Good" else ((if (er>=1) "⚠️ Average" else "❌ Low")))))));

}

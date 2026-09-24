package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_cash_on_cash_return_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("down" to J.dbl(inp.num("down")), "closing" to J.dbl(inp.num("closing")), "rent" to J.dbl(inp.num("rent")), "pi" to J.dbl(inp.num("pi")), "tax" to J.dbl(inp.num("tax")), "ins" to J.dbl(inp.num("ins")), "other" to J.dbl(inp.num("other"))))
  var r=cash_on_cash_return_calculator_core(p);
  out["resCoC"] = J.s(cash_on_cash_return_calculator_fmtP(r.num("coc"),2));
  out["resCoCSub"] = J.s((if (r.num("coc")>=8) "✅ Above the 8% investor target" else ((if (r.num("coc")>=0) "⚠️ Below the 8% target" else "❌ Negative return on cash"))));
  out["resInv"] = J.s(cash_on_cash_return_calculator_fmtM(r.num("invested")));
  out["resCF"] = J.s(((if (r.num("cf")>=0) "" else "−"))+cash_on_cash_return_calculator_fmtM(kotlin.math.abs(r.num("cf")))+"/yr");
  out["resMo"] = J.s(((if (r.num("mo")>=0) "" else "−"))+cash_on_cash_return_calculator_fmtM(kotlin.math.abs(r.num("mo")))+"/mo");
  out["resNote"] = J.s("Each ${'$'}10k invested returns "+cash_on_cash_return_calculator_fmtM(r.num("coc")/100*10000)+"/yr");
  out["shareText"] = J.s("Cash-on-cash "+J.toFixed((r.num("coc")), (2).toInt())+"% on "+cash_on_cash_return_calculator_fmtM(r.num("invested"))+" invested");

    return out
}

private fun cash_on_cash_return_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun cash_on_cash_return_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+cash_on_cash_return_calculator_fmt0(kotlin.math.abs(n));
}

private fun cash_on_cash_return_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun cash_on_cash_return_calculator_core(p: Inp): Inp {
    var p = p;
  var invested=p.num("down")+p.num("closing");
  var cf=(p.num("rent")-p.num("pi")-p.num("tax")-p.num("ins")-p.num("other"))*12;
  var coc=(if (invested>0) cf/invested*100 else 0.0);
  return Inp(mapOf("invested" to J.dbl(invested), "cf" to J.dbl(cf), "mo" to J.dbl(cf/12), "coc" to J.dbl(coc)));

}

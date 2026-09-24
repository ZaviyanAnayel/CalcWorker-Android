package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_brrrr_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("purchase" to J.dbl(inp.num("purchase")), "rehab" to J.dbl(inp.num("rehab")), "arv" to J.dbl(inp.num("arv")), "ltv" to J.dbl(inp.num("ltv")), "rate" to J.dbl(inp.num("rate")), "rent" to J.dbl(inp.num("rent")), "costs" to J.dbl(inp.num("costs"))))
  var r=brrrr_calculator_core(p);
  out["resLeft"] = J.s(((if (r.num("left")>=0) "" else "−"))+brrrr_calculator_fmtM(kotlin.math.abs(r.num("left"))));
  
  if(r.num("left")<=0){out["s"] = J.s("🚀 Infinite return — all cash recovered!");}
  else{out["s"] = J.s(brrrr_calculator_fmtP(r.num("left")/p.num("arv")*100,1)+" of ARV left invested");}
  out["resInv"] = J.s(brrrr_calculator_fmtM(r.num("invested")));out["resLoan"] = J.s(brrrr_calculator_fmtM(r.num("loan")));
  out["resPI"] = J.s(brrrr_calculator_fmtM(r.num("pi"))+"/mo");out["resCF"] = J.s(((if (r.num("cf")>=0) "" else "−"))+brrrr_calculator_fmtM(kotlin.math.abs(r.num("cf")))+"/mo");
  out["resCoC"] = J.s((if (!J.isFin(r.num("coc"))) "∞ Infinite" else brrrr_calculator_fmtP(r.num("coc"),1)));
  out["shareText"] = J.s("BRRRR: "+brrrr_calculator_fmtM(r.num("left"))+" left in, cash flow "+brrrr_calculator_fmtM(r.num("cf"))+"/mo");

    return out
}

private fun brrrr_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun brrrr_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+brrrr_calculator_fmt0(kotlin.math.abs(n));
}

private fun brrrr_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun brrrr_calculator_core(p: Inp): Inp {
    var p = p;
  var invested=p.num("purchase")+p.num("rehab");
  var loan=p.num("arv")*p.num("ltv")/100;
  var left=invested-loan;
  var pi=loan*brrrr_calculator_piFactor(p.num("rate"),30);
  var cf=p.num("rent")-pi-p.num("costs");
  var annual=cf*12;
  var coc=(if (left>0) annual/left*100 else ((if (annual>0) Double.POSITIVE_INFINITY else -Double.POSITIVE_INFINITY)));
  return Inp(mapOf("invested" to J.dbl(invested), "loan" to J.dbl(loan), "left" to J.dbl(left), "pi" to J.dbl(pi), "cf" to J.dbl(cf), "annual" to J.dbl(annual), "coc" to J.dbl(coc)));

}

private fun brrrr_calculator_piFactor(ratePct: Number, years: Number): Double {
    var ratePct = ratePct.toDouble();
    var years = years.toDouble();
  var r=ratePct/100/12; var n=years*12;return (if (r>0) r*J.pw(J.dbl(1+r), J.dbl(n))/(J.pw(J.dbl(1+r), J.dbl(n))-1) else 1/n);

}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_house_affordability_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("income" to J.dbl(inp.num("income")), "debts" to J.dbl(inp.num("debts")), "down" to J.dbl(inp.num("down")), "rate" to J.dbl(inp.num("rate")), "term" to J.dbl(inp.num("term")), "taxRate" to J.dbl(inp.num("taxRate")), "ins" to J.dbl(inp.num("ins"))))
  var r=house_affordability_calculator_core(p);
  out["resPrice"] = J.s(house_affordability_calculator_fmtM(r.num("price")));
  out["resPriceSub"] = J.s("On a "+house_affordability_calculator_fmtM(r.num("budget"))+"/mo housing budget");
  out["resLoan"] = J.s(house_affordability_calculator_fmtM(r.num("loan")));out["resPI"] = J.s(house_affordability_calculator_fmtM(r.num("pi"))+"/mo");out["resTax"] = J.s(house_affordability_calculator_fmtM(r.num("tax"))+"/mo");
  out["resIns"] = J.s(house_affordability_calculator_fmtM(r.num("ins"))+"/mo");out["resPITI"] = J.s(house_affordability_calculator_fmtM(r.num("piti"))+"/mo");out["resBudget"] = J.s(house_affordability_calculator_fmtM(r.num("budget"))+"/mo");
  out["shareText"] = J.s("Max home price "+house_affordability_calculator_fmtM(r.num("price"))+" on "+house_affordability_calculator_fmtM(p.num("income"))+"/yr income");

    return out
}

private fun house_affordability_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+house_affordability_calculator_fmt0(kotlin.math.abs(n));
}

private fun house_affordability_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun house_affordability_calculator_core(p: Inp): Inp {
    var p = p;
  var f=house_affordability_calculator_piFactor(p.num("rate"),p.num("term"));
  var budget=J.min(p.num("income")/12*0.28,p.num("income")/12*0.36-p.num("debts"));
  budget=J.max(0,budget);
  var slope=f+p.num("taxRate")/100/12;
  var price=J.max(p.num("down"),budget*120);
  var i: Int = 0; while (i<60) {
    var taxMo=price*p.num("taxRate")/100/12; var insMo=p.num("ins")/12;
    var loan=J.max(0,price-p.num("down"));
    var piti=loan*f+taxMo+insMo;
    price+=(budget-piti)/slope;
    if(price<0){price=0.0;break;}
  ; i += 1}
  var loan=J.max(0,price-p.num("down"));
  var pi=loan*f; var taxMo=price*p.num("taxRate")/100/12; var insMo=p.num("ins")/12;
  return Inp(mapOf("price" to J.dbl(price), "loan" to J.dbl(loan), "pi" to J.dbl(pi), "tax" to J.dbl(taxMo), "ins" to J.dbl(insMo), "piti" to J.dbl(pi+taxMo+insMo), "budget" to J.dbl(budget)));

}

private fun house_affordability_calculator_piFactor(ratePct: Number, years: Number): Double {
    var ratePct = ratePct.toDouble();
    var years = years.toDouble();
  var r=ratePct/100/12; var n=years*12;return (if (r>0) r*J.pw(J.dbl(1+r), J.dbl(n))/(J.pw(J.dbl(1+r), J.dbl(n))-1) else 1/n);

}

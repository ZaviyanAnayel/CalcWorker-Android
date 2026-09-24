package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_s_corp_tax_savings_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("profit" to J.dbl(inp.num("profit")), "salary" to J.dbl(inp.num("salary")), "adminCost" to J.dbl(inp.num("adminCost"))))
  var r=s_corp_tax_savings_calculator_core(p);
  out["resSave"] = J.s(((if (r.num("net")>=0) "" else "−"))+s_corp_tax_savings_calculator_fmtM(kotlin.math.abs(r.num("net"))));
  
  out["resWorth"] = J.s((if (r.num("worth") != 0.0) "✅ Election likely worth it" else "❌ Costs outweigh savings"));
  
  out["resLLC"] = J.s(s_corp_tax_savings_calculator_fmtM(r.num("llc")));out["resSCorp"] = J.s(s_corp_tax_savings_calculator_fmtM(r.num("scorp")));out["resGross"] = J.s(s_corp_tax_savings_calculator_fmtM(r.num("gross")));
  out["resCost"] = J.s(s_corp_tax_savings_calculator_fmtM(p.num("adminCost")));out["resDist"] = J.s(s_corp_tax_savings_calculator_fmtM(r.num("dist")));
  out["shareText"] = J.s("S-corp savings on "+s_corp_tax_savings_calculator_fmtM(p.num("profit"))+": "+s_corp_tax_savings_calculator_fmtM(r.num("net"))+"/yr net");

    return out
}

private fun s_corp_tax_savings_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+s_corp_tax_savings_calculator_fmt0(kotlin.math.abs(n));
}

private fun s_corp_tax_savings_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun s_corp_tax_savings_calculator_core(p: Inp): Inp {
    var p = p;
  var llc=s_corp_tax_savings_calculator_seTaxF(p.num("profit")).num("total");
  var scorpFica=p.num("salary")*0.153;
  var gross=llc-scorpFica;
  var net=gross-p.num("adminCost");
  return Inp(mapOf("llc" to J.dbl(llc), "scorp" to J.dbl(scorpFica), "gross" to J.dbl(gross), "net" to J.dbl(net), "dist" to J.dbl(J.max(0,p.num("profit")-p.num("salary"))), "worth" to J.dbl(net>0)));

}

private fun s_corp_tax_savings_calculator_seTaxF(net: Number): Inp {
    var net = net.toDouble();
  var base=net*0.9235;
  var ss=J.min(base,176100)*0.062*2; 
  var med=base*0.029;
  return Inp(mapOf("total" to J.dbl(ss+med), "base" to J.dbl(base), "ss" to J.dbl(ss), "med" to J.dbl(med)));

}

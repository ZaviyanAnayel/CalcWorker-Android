package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_fire_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var p=Inp(mapOf("age" to J.dbl(inp.num("age")), "retireAge" to J.dbl(inp.num("retireAge")), "savings" to J.dbl(inp.num("savings")), "contrib" to J.dbl(inp.num("contrib")), "expenses" to J.dbl(inp.num("expenses")), "ret" to J.dbl(inp.num("ret")), "swr" to J.dbl(inp.num("swr"))))
  var r=fire_calculator_core(p);
  out["resFire"] = J.s(fire_calculator_fmtM(r.num("fire")));
  out["resFireSub"] = J.s("= "+fire_calculator_fmtM(p.num("expenses"))+" annual expenses ÷ "+fire_calculator_fmtP(p.num("swr"),1)+" withdrawal rate");
  out["resProj"] = J.s(fire_calculator_fmtM(r.num("proj")));
  out["resGap"] = J.s(((if (r.num("gap")>0) "-" else ""))+fire_calculator_fmtM(kotlin.math.abs(r.num("gap")))+((if (r.num("gap")<=0) " surplus" else " shortfall")));
  out["resAge"] = J.s(J.toFixed((r.num("fiAge")), (1).toInt())+" years");
  out["resCov"] = J.s(fire_calculator_fmtM(r.num("proj")*(p.num("swr")/100))+" / yr sustainable");
  out["shareText"] = J.s("FIRE number "+fire_calculator_fmtM(r.num("fire"))+", projected "+fire_calculator_fmtM(r.num("proj"))+" by age "+p.num("retireAge")+", FI at ~"+J.toFixed((r.num("fiAge")), (1).toInt()));

    return out
}

private fun fire_calculator_fmtP(n: Number, d: Number): String {
    var n = n.toDouble();
    var d = d.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.toFixed((n), ((if (d==null) 1.0 else d)).toInt())+"%";
}

private fun fire_calculator_fmtM(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return ((if (n<0) "-${'$'}" else "${'$'}"))+fire_calculator_fmt0(kotlin.math.abs(n));
}

private fun fire_calculator_fmt0(n: Number): String {
    var n = n.toDouble();n=(if (J.isFin(n)) n else 0.0);return J.loc((n), (0).toInt(), (0).toInt());
}

private fun fire_calculator_core(p: Inp): Inp {
    var p = p;
  var y=J.max(0,p.num("retireAge")-p.num("age"));
  var r=p.num("ret")/100;
  var g=J.pw(J.dbl(1+r), J.dbl(y));
  var proj=p.num("savings")*g+((if (r>0) p.num("contrib")*(g-1)/r else p.num("contrib")*y));
  var fire=(if (p.num("swr")>0) p.num("expenses")/(p.num("swr")/100) else 0.0);
  var gap=fire-proj;
  var n=y;
  if(r>0){
    var lo = 0.0; var hi = 100.0;
    var i: Int = 0; while (i<80) {var m=(lo+hi)/2;var gm=J.pw(J.dbl(1+r), J.dbl(m));
      var v=p.num("savings")*gm+p.num("contrib")*(gm-1)/r;
      if(v>=fire)hi=m;else lo=m; i += 1}
    n=hi;
  }
  return Inp(mapOf("fire" to J.dbl(fire), "proj" to J.dbl(proj), "gap" to J.dbl(gap), "fiAge" to J.dbl(p.num("age")+n), "years" to J.dbl(y)));

}

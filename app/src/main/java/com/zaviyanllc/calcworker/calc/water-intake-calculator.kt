package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_water_intake_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var wt =J.orD((inp.num("weightLbs")), (150));
  var ex =J.orD((inp.num("exerciseMin")), (0));
  var clim =J.orD((inp.num("climateSelect")), (1.0));

  var baseOz = wt * 0.67;
  var sweatOz = (ex / 30) * 12;
  var totalOz = (baseOz + sweatOz) * clim;
  var liters = totalOz * 0.0295735;
  var glasses = J.round(totalOz / 8);
  var bottles =J.toFixed(((totalOz / 32)), (1).toInt());

  out["resOz"] = J.s(J.round(totalOz) + " fl oz");
  out["resLiters"] = J.s(J.toFixed((liters), (2).toInt()) + " Liters (" + ((if (liters >= 3.78) "~1 Gallon+" else "~" +J.toFixed(((liters/3.785)), (1).toInt()) + " Gallon")) + ")");
  out["resGlasses"] = J.s(glasses + " Glasses");
  out["resBottles"] = J.s(bottles + " Bottles");
  out["resSweat"] = J.s("+" + J.round(sweatOz) + " fl oz");
  out["resBaseReq"] = J.s(J.round(baseOz) + " fl oz");

    return out
}

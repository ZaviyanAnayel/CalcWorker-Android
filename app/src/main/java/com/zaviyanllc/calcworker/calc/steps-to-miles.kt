package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_steps_to_miles(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    val steps =J.orD((inp.num("stepInput")), (0));
    val height =J.orD((inp.num("heightInput")), (68));
    val strideInches = height * 0.414;
    val miles = (steps * strideInches) / 63360;
    val km = miles * 1.60934;
    val calories = J.round(steps * 0.04);

    out["milesResult"] = J.s(J.toFixed((miles), (2).toInt()) + " Miles");
    out["kmResult"] = J.s("(" +J.toFixed((km), (2).toInt()) + " Kilometers)");
    out["strideResult"] = J.s(J.toFixed((strideInches), (1).toInt()) + " in");
    out["calResult"] = J.s("~" + calories + " kcal");
  
    return out
}

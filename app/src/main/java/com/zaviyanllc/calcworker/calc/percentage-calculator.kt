package com.zaviyanllc.calcworker.calc

import kotlin.math.*

private var currentMode: String = "mode1"

fun calc_percentage_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  
  
  
  

  if (currentMode == "mode1") {
    val p =J.orD((inp.num("m1_pct")), (0));
    val x =J.orD((inp.num("m1_val")), (0));
    val ans = (p / 100) * x;
    out["resLabel"] = J.s("Calculated Value");
    out["resPrimary"] = J.s(J.locDf((J.toNum(J.toFixed((ans), (4).toInt())))));
    out["resSummary"] = J.s("${p}% of ${x} = ${J.locDf((J.toNum(J.toFixed((ans), (4).toInt()))))}");
    out["resFormula"] = J.s("(${p} ÷ 100) × ${x} = ${(p/100)} × ${x} = <span class=\"pct-step-math\">${J.locDf((J.toNum(J.toFixed((ans), (4).toInt()))))}</span>");
  } else if (currentMode == "mode2") {
    val x =J.orD((inp.num("m2_x")), (0));
    val y =J.orD((inp.num("m2_y")), (0));
    if (y == 0.0) {
      out["resPrimary"] = J.s("0%");
      out["resSummary"] = J.s("Division by zero is null.");
      out["resFormula"] = J.s("Base value (Y) cannot be 0.");
      return out;
    }
    val ans = (x / y) * 100;
    out["resLabel"] = J.s("Calculated Percentage");
    out["resPrimary"] = J.s(J.toNum(J.toFixed((ans), (4).toInt())) + "%");
    out["resSummary"] = J.s("${x} is ${J.toNum(J.toFixed((ans), (4).toInt()))}% of ${y}");
    out["resFormula"] = J.s("(${x} ÷ ${y}) × 100 = ${J.toFixed(((x/y)), (4).toInt())} × 100 = <span class=\"pct-step-math\">${J.toNum(J.toFixed((ans), (4).toInt()))}%</span>");
  } else if (currentMode == "mode3") {
    val initial =J.orD((inp.num("m3_initial")), (0));
    val finalVal =J.orD((inp.num("m3_final")), (0));
    if (initial == 0.0) {
      out["resPrimary"] = J.s("0%");
      out["resSummary"] = J.s("Initial value cannot be zero.");
      out["resFormula"] = J.s("Formula requires a non-zero initial baseline.");
      return out;
    }
    val change = finalVal - initial;
    val pctChange = (change / kotlin.math.abs(initial)) * 100;
    val isIncrease = change >= 0;
    
    out["resLabel"] = J.s((if (J.truthy(isIncrease)) "Percentage Increase" else "Percentage Decrease"));
    out["resPrimary"] = J.s(((if (J.truthy(isIncrease)) "+" else "")) + J.toNum(J.toFixed((pctChange), (2).toInt())) + "%");
    
    out["resSummary"] = J.s("Going from ${initial} to ${finalVal} is an absolute change of ${change} (${kotlin.math.abs(J.toNum(J.toFixed((pctChange), (2).toInt())))}% ${(if (J.truthy(isIncrease)) "increase" else "decrease")}).");
    out["resFormula"] = J.s("((${finalVal} - ${initial}) ÷ |${initial}|) × 100 = (${change} ÷ ${kotlin.math.abs(initial)}) × 100 = <span class=\"pct-step-math\">${J.toNum(J.toFixed((pctChange), (2).toInt()))}%</span>");
  } else if (currentMode == "mode4") {
    val a =J.orD((inp.num("m4_a")), (0));
    val b =J.orD((inp.num("m4_b")), (0));
    val avg = (a + b) / 2;
    if (avg == 0.0) {
      out["resPrimary"] = J.s("0%");
      out["resSummary"] = J.s("Average of both values is zero.");
      out["resFormula"] = J.s("Formula requires a non-zero average.");
      return out;
    }
    val diff = kotlin.math.abs(a - b);
    val pctDiff = (diff / kotlin.math.abs(avg)) * 100;
    out["resLabel"] = J.s("Percentage Difference");
    out["resPrimary"] = J.s(J.toNum(J.toFixed((pctDiff), (2).toInt())) + "%");
    
    out["resSummary"] = J.s("The relative percentage difference between ${a} and ${b} is ${J.toNum(J.toFixed((pctDiff), (2).toInt()))}%.");
    out["resFormula"] = J.s("|${a} - ${b}| ÷ ((${a} + ${b}) ÷ 2) × 100 = ${diff} ÷ ${avg} × 100 = <span class=\"pct-step-math\">${J.toNum(J.toFixed((pctDiff), (2).toInt()))}%</span>");
  }

    return out
}

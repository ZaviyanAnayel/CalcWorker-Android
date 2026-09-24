package com.zaviyanllc.calcworker.calc

import kotlin.math.*

private var unitMode: String = "metric"

fun calc_bmi_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var heightMeters = 0.0;
  var weightKg = 0.0;

  if (unitMode == "metric") {
    val cm =J.orD((inp.num("heightCm")), (0));
    val kg =J.orD((inp.num("weightKg")), (0));
    heightMeters = cm / 100;
    weightKg = kg;
  } else {
    val ft =J.orD((inp.num("heightFt")), (0));
    val inch =J.orD((inp.num("heightIn")), (0));
    val lbs =J.orD((inp.num("weightLbs")), (0));
    val totalInches = (ft * 12) + inch;
    heightMeters = totalInches * 0.0254;
    weightKg = lbs * 0.45359237;
  }

  if (J.orD((heightMeters <= 0), (weightKg <= 0))) return out;

  val bmi = weightKg / (heightMeters * heightMeters);
  
  
  

  out["resVal"] = J.s(J.toFixed((bmi), (1).toInt()));

  var category = "";
  var color = "#10b981";
  var meterPct = 50.0;

  if (bmi < 18.5) {
    category = "Underweight";
    color = "#38bdf8";
    meterPct = J.max(5, (bmi / 18.5) * 25);
  } else if (bmi < 25.0) {
    category = "Normal Weight";
    color = "#10b981";
    meterPct = 25 + (((bmi - 18.5) / 6.4) * 35);
  } else if (bmi < 30.0) {
    category = "Overweight";
    color = "#f59e0b";
    meterPct = 60 + (((bmi - 25.0) / 4.9) * 20);
  } else {
    category = "Obese Class";
    color = "#ef4444";
    meterPct = J.min(96, 80 + (((bmi - 30.0) / 15.0) * 16));
  }

  
  
  out["resCat"] = J.s(category);
  

  
  val minKg = 18.5 * (heightMeters * heightMeters);
  val maxKg = 24.9 * (heightMeters * heightMeters);
  val minLbs = minKg * 2.20462;
  val maxLbs = maxKg * 2.20462;

  out["resIdealWeight"] = J.s("${J.toFixed((minKg), (1).toInt())} kg – ${J.toFixed((maxKg), (1).toInt())} kg (${J.toFixed((minLbs), (1).toInt())} lbs – ${J.toFixed((maxLbs), (1).toInt())} lbs)");

    return out
}

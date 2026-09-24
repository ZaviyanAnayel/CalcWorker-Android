package com.zaviyanllc.calcworker.calc

import kotlin.math.*

private var currentGender: String = "male"

fun calc_calorie_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  val age =J.orD((inp.num("calAge")), (0));
  val weight =J.orD((inp.num("calWeight")), (0));
  val height =J.orD((inp.num("calHeight")), (0));
  val activity =J.orD((inp.num("calActivity")), (1.2));

  if (J.orD((age <= 0), (weight <= 0 || height <= 0))) return out;

  
  
  
  var bmr = (10 * weight) + (6.25 * height) - (5 * age);
  bmr =(if (currentGender == "male") bmr + 5 else bmr - 161);

  val tdee = J.round(bmr * activity);
  val fatLossCal = J.max(1200, tdee - 500);
  val mildLossCal = J.max(1200, tdee - 250);
  val bulkCal = tdee + 300;

  out["resTdee"] = J.s(J.locDf((tdee)) + " kcal");
  out["resBmr"] = J.s("Basal Metabolic Rate (BMR): ${J.locDf((J.round(bmr)))} kcal/day");

  out["planFatLoss"] = J.s(J.locDf((fatLossCal)));
  out["planMildLoss"] = J.s(J.locDf((mildLossCal)));
  out["planBulk"] = J.s(J.locDf((bulkCal)));
  out["planMaintain"] = J.s(J.locDf((tdee)));

  
  out["macroTotalLabel"] = J.s(J.locDf((fatLossCal)) + " kcal");
  val protGrams = J.round((fatLossCal * 0.35) / 4);
  val fatGrams = J.round((fatLossCal * 0.25) / 9);
  val carbGrams = J.round((fatLossCal * 0.40) / 4);

  out["macroProt"] = J.s(protGrams + "g");
  out["macroFat"] = J.s(fatGrams + "g");
  out["macroCarb"] = J.s(carbGrams + "g");

    return out
}

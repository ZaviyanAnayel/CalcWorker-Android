package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_capital_gains_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val buy =J.orD((inp.num("purchasePrice")), (0));
            val sell =J.orD((inp.num("salePrice")), (0));
            val holding = inp.str("holdingPeriod");
            val income =J.orD((inp.num("taxableIncome")), (0));
            val status = inp.str("filingStatus");
            val stateRate = (J.orD((inp.num("stateTaxRate")), (0))) / 100;

            val gain = J.max(0, sell - buy);
            var fedRate = 0.0;
            var niitRate = 0.0;

            if (holding == "long") {
                var zeroLimit =(if (status == "married") 94050 else 47025);
                var fifteenLimit =(if (status == "married") 583750 else 518900);
                var totalInc = income + gain;
                if (totalInc <= zeroLimit) fedRate = 0.0;
                else if (totalInc <= fifteenLimit) fedRate = 0.15;
                else fedRate = 0.20;
            } else {
                if (income > 600000) fedRate = 0.37;
                else if (income > 240000) fedRate = 0.35;
                else if (income > 190000) fedRate = 0.32;
                else if (income > 100000) fedRate = 0.24;
                else if (income > 47000) fedRate = 0.22;
                else fedRate = 0.12;
            }

            var niitThreshold =(if (status == "married") 250000 else 200000);
            if ((income + gain) > niitThreshold) {
                val subjectToNiit = J.min(gain, (income + gain) - niitThreshold);
                niitRate = (subjectToNiit * 0.038) / (J.orD((gain), (1)));
            }

            val fedTax = gain * fedRate;
            val niitTax = gain * niitRate;
            val stateTax = gain * stateRate;
            val totalTax = fedTax + niitTax + stateTax;
            val afterTax = sell - totalTax;
            val effRate =(if (gain > 0) (totalTax / gain) * 100 else 0.0);

            out["resNetGain"] = J.s("${'$'}" +J.loc((gain), (2).toInt(), (2).toInt()));
            out["resTotalTax"] = J.s("${'$'}" +J.loc((totalTax), (2).toInt(), (2).toInt()));
            out["resEffectiveRate"] = J.s(J.toFixed((effRate), (1).toInt()) + "%");
            out["resFedTax"] = J.s("${'$'}" +J.loc((fedTax), (2).toInt(), (2).toInt()));
            out["resNiitTax"] = J.s("${'$'}" +J.loc((niitTax), (2).toInt(), (2).toInt()));
            out["resStateTax"] = J.s("${'$'}" +J.loc((stateTax), (2).toInt(), (2).toInt()));
            out["resAfterTaxProfit"] = J.s("${'$'}" +J.loc((afterTax), (2).toInt(), (2).toInt()));
        
  } catch (e: Exception) {
    
  }

    return out
}

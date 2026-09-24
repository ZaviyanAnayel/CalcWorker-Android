package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_freelance_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    try {
      val gross =J.orD((inp.num("annualGross")), (0));
      val expenses =J.orD((inp.num("businessExpenses")), (0));
      val status =J.orS((inp.str("filingStatus")), ("single"));
      val stateRate =J.orD((inp.num("stateSelect")), (0.05));
      val retirement =J.orD((inp.num("retirementDeduction")), (0));

      val netProfit = J.max(0, gross - expenses);

      val seTaxable = netProfit * 0.9235;
      val ssCap = 176100;
      val ssTax = J.min(seTaxable, ssCap) * 0.124;
      val medTax = seTaxable * 0.029;
      val totalSETax = ssTax + medTax;
      val halfSETax = totalSETax * 0.5;

      val agi = J.max(0, netProfit - halfSETax - retirement);

      var stdDeduction = 15000.0;
      if (status == "married") stdDeduction = 30000.0;
      else if (status == "hoh") stdDeduction = 22500.0;

      val qbiDeduction = netProfit * 0.20;
      val taxableIncome = J.max(0, agi - stdDeduction - qbiDeduction);

      var fedTax = 0.0;
      if (status == "married") {
        if (taxableIncome <= 23850) fedTax = taxableIncome * 0.10;
        else if (taxableIncome <= 96950) fedTax = 2385 + (taxableIncome - 23850) * 0.12;
        else if (taxableIncome <= 206700) fedTax = 11157 + (taxableIncome - 96950) * 0.22;
        else fedTax = 35302 + (taxableIncome - 206700) * 0.24;
      } else {
        if (taxableIncome <= 11925) fedTax = taxableIncome * 0.10;
        else if (taxableIncome <= 48475) fedTax = 1192.5 + (taxableIncome - 11925) * 0.12;
        else if (taxableIncome <= 103350) fedTax = 5578.5 + (taxableIncome - 48475) * 0.22;
        else fedTax = 17651 + (taxableIncome - 103350) * 0.24;
      }

      val stateTax = J.max(0, (netProfit - stdDeduction) * stateRate);

      val totalTaxes = totalSETax + fedTax + stateTax;
      val quarterly = totalTaxes / 4;
      val takeHome = J.max(0, netProfit - totalTaxes);
      val effectiveRate =(if (netProfit > 0) (totalTaxes / netProfit) * 100 else 0.0);

      out["kpiQuarterly"] = J.s("${'$'}" +J.toFixed((quarterly), (2).toInt()));
      out["effectiveSub"] = J.s("Effective Total Tax Rate: " +J.toFixed((effectiveRate), (1).toInt()) + "%");
      out["kpiSETax"] = J.s("${'$'}" +J.locDf((J.round(totalSETax))));
      out["kpiFedTax"] = J.s("${'$'}" +J.locDf((J.round(fedTax))));
      out["kpiQBI"] = J.s("${'$'}" +J.locDf((J.round(qbiDeduction))));
      out["kpiTakeHome"] = J.s("${'$'}" +J.locDf((J.round(takeHome))));

      out["rowNetProfit"] = J.s("${'$'}" +J.locDf((J.round(netProfit))));
      out["rowSE"] = J.s("${'$'}" +J.locDf((J.round(totalSETax))));
      out["rowFed"] = J.s("${'$'}" +J.locDf((J.round(fedTax))));
      out["rowState"] = J.s("${'$'}" +J.locDf((J.round(stateTax))));
      out["rowTotalTax"] = J.s("${'$'}" +J.locDf((J.round(totalTaxes))));
      out["rowTakeHome"] = J.s("${'$'}" +J.locDf((J.round(takeHome))));
    } catch (err: Exception) {
      
    }
  
    return out
}

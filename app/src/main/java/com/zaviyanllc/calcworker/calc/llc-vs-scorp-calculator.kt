package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_llc_vs_scorp_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val profit =J.orD((inp.num("netBusinessProfit")), (0));
            val salary =J.orD((inp.num("reasonableSalary")), (0));
            val overhead =J.orD((inp.num("scorpOverheadAnnual")), (0));

            
            val seTaxBase = profit * 0.9235;
            val defaultLlcSeTax = seTaxBase * 0.153;

            
            val scorpFica = salary * 0.153;
            val distributions = J.max(0, profit - salary);
            val grossFicaSaved = J.max(0, defaultLlcSeTax - scorpFica);
            val netAnnualSavings = grossFicaSaved - overhead;

            val salaryRatio =(if (profit > 0) (salary / profit) * 100 else 0.0);
            var risk = "Low Risk (Balanced 50/50+ ratio)";
            if (salaryRatio < 35) risk = "High Audit Risk (Salary too low for IRS RC standards)";
            else if (salaryRatio < 45) risk = "Moderate Risk";

            out["resNetTaxSaved"] = J.s((if (netAnnualSavings > 0) "+${'$'}" +J.locDf((J.round(netAnnualSavings))) + " / yr" else "${'$'}0 (Overhead exceeds savings)"));
            out["resFicaSavedGross"] = J.s("+${'$'}" +J.locDf((J.round(grossFicaSaved))));
            out["resDistributionShare"] = J.s("${'$'}" +J.locDf((J.round(distributions))) + " (0% FICA Tax)");
            out["resDefaultLlcTax"] = J.s("${'$'}" +J.locDf((J.round(defaultLlcSeTax))) + " (All profit taxed at 15.3%)");
            out["resScorpFica"] = J.s("${'$'}" +J.locDf((J.round(scorpFica))) + " (FICA paid only on W-2)");
            out["resAdminOverhead"] = J.s("-${'$'}" +J.locDf((J.round(overhead))) + " (Gusto payroll + CPA 1120-S)");
            out["resIrsAuditRisk"] = J.s(risk + " (" +J.toFixed((salaryRatio), (0).toInt()) + "% salary / " +J.toFixed(((100-salaryRatio)), (0).toInt()) + "% distribution)");
        
  } catch (e: Exception) {
    
  }

    return out
}

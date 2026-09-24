package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_simple_interest_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val p =J.orD((inp.num("principalAmountVal")), (0));
            val r = (J.orD((inp.num("annualInterestRate")), (0))) / 100;
            val t =J.orD((inp.num("investmentTermYears")), (0));
            val n =J.orD((inp.num("compoundFrequency")), (12));

            
            val simpleInterest = p * r * t;
            val simpleBalance = p + simpleInterest;

            
            val compoundBalance = p * J.pw(J.dbl(1 + (r / n)), J.dbl(n * t));
            val compoundInterest = compoundBalance - p;

            val advantage = compoundBalance - simpleBalance;
            val apy = (J.pw(J.dbl(1 + (r / n)), J.dbl(n)) - 1) * 100;
            val growthMult =(if (p > 0) compoundBalance / p else 0.0);

            out["resCompoundTotalBalance"] = J.s("${'$'}" +J.locDf((J.round(compoundBalance))));
            out["resSimpleTotalBalance"] = J.s("${'$'}" +J.locDf((J.round(simpleBalance))));
            out["resCompoundAdvantage"] = J.s("+${'$'}" +J.locDf((J.round(advantage))));

            out["resSimpleInterestEarned"] = J.s("${'$'}" +J.locDf((J.round(simpleInterest))));
            out["resCompoundInterestEarned"] = J.s("${'$'}" +J.locDf((J.round(compoundInterest))));
            out["resEffectiveApyRate"] = J.s(J.toFixed((apy), (2).toInt()) + "% APY");
            out["resPrincipalMultiple"] = J.s(J.toFixed((growthMult), (2).toInt()) + "x Initial Principal");out["resSummary"] = J.s("Simple vs Compound Comparison (${t} years at ${J.toFixed(((r*100)), (1).toInt())}%): Simple Total: ${'$'}${J.locDf((J.round(simpleBalance)))} (${'$'}${J.locDf((J.round(simpleInterest)))} interest). Compound Total: ${'$'}${J.locDf((J.round(compoundBalance)))} (${'$'}${J.locDf((J.round(compoundInterest)))} interest). Compounding adds ${'$'}${J.locDf((J.round(advantage)))} in bonus wealth!"); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

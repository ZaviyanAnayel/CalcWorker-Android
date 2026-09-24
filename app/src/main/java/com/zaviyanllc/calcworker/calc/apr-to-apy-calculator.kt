package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_apr_to_apy_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val apr = (J.orD((inp.num("interestRateInput")), (0))) / 100;
            val n =J.orD((J.piD(inp.str("compoundingFrequency"), 10)), (365));
            val deposit =J.orD((inp.num("sampleDepositBalance")), (0));

            val apy = J.pw(J.dbl(1 + (apr / n)), J.dbl(n)) - 1;
            val annualEarned = deposit * apy;
            val simpleEarned = deposit * apr;
            val compoundBonus = J.max(0, annualEarned - simpleEarned);

            
            val dailyApy = (J.pw(J.dbl(1 + (apr / 365)), J.dbl(365)) - 1) * 100;

            out["resEffectiveApy"] = J.s(J.toFixed(((apy * 100)), (3).toInt()) + "% APY");
            out["resAnnualInterestEarned"] = J.s("${'$'}" +J.loc((annualEarned), (2).toInt(), (2).toInt()));
            out["resCompoundingBonus"] = J.s("+${'$'}" +J.toFixed((compoundBonus), (2).toInt()) + " / yr");
            out["resNominalApr"] = J.s(J.toFixed(((apr * 100)), (2).toInt()) + "% nominal");
            out["resSimpleInterest"] = J.s("${'$'}" +J.toFixed((simpleEarned), (2).toInt()) + " (Linear interest)");
            out["resDailyApy"] = J.s(J.toFixed((dailyApy), (3).toInt()) + "% APY (Daily frequency)");
            out["resTisaNotice"] = J.s("Reg DD requires banks to disclose APY on deposit accounts");
        
  } catch (e: Exception) {
    
  }

    return out
}

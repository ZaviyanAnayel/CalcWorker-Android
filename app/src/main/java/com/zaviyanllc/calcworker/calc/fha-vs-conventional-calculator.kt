package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_fha_vs_conventional_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val price =J.orD((inp.num("homePrice")), (0));
            val downPct = (J.orD((inp.num("downPaymentPct")), (5))) / 100;
            val credit =J.orD((J.piD(inp.str("creditScoreTier"), 10)), (720));
            val convApr = (J.orD((inp.num("conventionalRate")), (6.85))) / 100 / 12;
            val fhaApr = (J.orD((inp.num("fhaRate")), (6.35))) / 100 / 12;

            val downAmt = price * downPct;
            val baseLoan = price - downAmt;
            val n = 360;

            
            val convPmt = (baseLoan * (convApr * J.pw(J.dbl(1 + convApr), J.dbl(n)))) / (J.pw(J.dbl(1 + convApr), J.dbl(n)) - 1);
            var pmiRate = 0.005; 
            if (credit >= 760) pmiRate = 0.0035;
            else if (credit >= 720) pmiRate = 0.0048;
            else if (credit >= 680) pmiRate = 0.0075;
            else pmiRate = 0.011;
            val monthlyPmi = (baseLoan * pmiRate) / 12;
            val convTotalMo = convPmt + ((if (downPct < 0.20) monthlyPmi else 0.0));

            
            val upfrontMip = baseLoan * 0.0175;
            val fhaTotalLoan = baseLoan + upfrontMip;
            val fhaPmt = (fhaTotalLoan * (fhaApr * J.pw(J.dbl(1 + fhaApr), J.dbl(n)))) / (J.pw(J.dbl(1 + fhaApr), J.dbl(n)) - 1);
            val monthlyMip = (fhaTotalLoan * 0.0055) / 12;
            val fhaTotalMo = fhaPmt + monthlyMip;

            val diff = kotlin.math.abs(convTotalMo - fhaTotalMo);
            val cheaper =(if (convTotalMo < fhaTotalMo) "Conventional Saves ${'$'}" + J.round(diff) + "/mo" else "FHA Saves ${'$'}" + J.round(diff) + "/mo");

            out["resConvMonthly"] = J.s("${'$'}" +J.locDf((J.round(convTotalMo))) + " / mo");
            out["resFhaMonthly"] = J.s("${'$'}" +J.locDf((J.round(fhaTotalMo))) + " / mo");
            out["resCheaperOption"] = J.s(cheaper);
            out["resFhaUpfrontMip"] = J.s("${'$'}" +J.locDf((J.round(upfrontMip))) + " (Financed into loan)");
            out["resMonthlyInsurance"] = J.s("Conv PMI: ${'$'}" + J.round(monthlyPmi) + "/mo | FHA MIP: ${'$'}" + J.round(monthlyMip) + "/mo");
            out["resFiveYearCost"] = J.s("Conv: ${'$'}" +J.locDf((J.round(convTotalMo * 60))) + " | FHA: ${'$'}" +J.locDf((J.round(fhaTotalMo * 60))));
            out["resPmiRemoval"] = J.s("Conv drops at 80% LTV | FHA lasts life of loan (with <10% down)");
        
  } catch (e: Exception) {
    
  }

    return out
}

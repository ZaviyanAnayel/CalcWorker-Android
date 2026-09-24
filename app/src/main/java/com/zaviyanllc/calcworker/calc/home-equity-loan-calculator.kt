package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_home_equity_loan_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val val_ =J.orD((inp.num("homeMarketValue")), (1));
            val mort1 =J.orD((inp.num("currentMortgageBalance")), (0));
            val borrow =J.orD((inp.num("desiredEquityAmount")), (0));
            val fixedRate = (J.orD((inp.num("fixedLoanRate")), (8.25))) / 100 / 12;
            val helocRate = (J.orD((inp.num("helocVariableRate")), (8.75))) / 100 / 12;
            val fixedYears =J.orD((J.piD(inp.str("fixedLoanTermYears"), 10)), (15));

            val maxLoanCap = (val_ * 0.85) - mort1;
            val maxBorrow = J.max(0, maxLoanCap);
            val cltv = ((mort1 + borrow) / val_) * 100;

            
            val nFixed = fixedYears * 12;
            val fixedPmt = (borrow * (fixedRate * J.pw(J.dbl(1 + fixedRate), J.dbl(nFixed)))) / (J.pw(J.dbl(1 + fixedRate), J.dbl(nFixed)) - 1);
            val totalFixedInt = (fixedPmt * nFixed) - borrow;

            
            val helocDrawMo = borrow * helocRate;
            
            val nHelocRepay = 240;
            val helocRepayPmt = (borrow * (helocRate * J.pw(J.dbl(1 + helocRate), J.dbl(nHelocRepay)))) / (J.pw(J.dbl(1 + helocRate), J.dbl(nHelocRepay)) - 1);
            val pmtJump = helocRepayPmt - helocDrawMo;

            out["resMaxBorrowable"] = J.s("${'$'}" +J.locDf((J.round(maxBorrow))));
            out["resFixedMonthlyPmt"] = J.s("${'$'}" +J.locDf((J.round(fixedPmt))) + " / mo (Fixed)");
            out["resHelocDrawPmt"] = J.s("${'$'}" +J.locDf((J.round(helocDrawMo))) + " / mo (Interest Only)");
            out["resCurrentCltv"] = J.s(J.toFixed((cltv), (1).toInt()) + "% (Lenders limit to 80-85%)");
            out["resFixedTotalInt"] = J.s("${'$'}" +J.locDf((J.round(totalFixedInt))) + " over " + fixedYears + " years");
            out["resHelocAmortPmt"] = J.s("${'$'}" +J.locDf((J.round(helocRepayPmt))) + " / mo during 20-yr repayment");
            out["resShockAlert"] = J.s("+${'$'}" +J.locDf((J.round(pmtJump))) + " monthly payment surge when 10-yr draw period ends!");
        
  } catch (e: Exception) {
    
  }

    return out
}

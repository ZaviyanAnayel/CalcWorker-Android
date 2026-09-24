package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_net_worth_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val cash =J.orD((inp.num("cashBankAccounts")), (0));
            val retire =J.orD((inp.num("retirementAccounts")), (0));
            val invest =J.orD((inp.num("brokerageCrypto")), (0));
            val reVal =J.orD((inp.num("realEstateValue")), (0));
            val carVal =J.orD((inp.num("vehicleValue")), (0));

            val mort =J.orD((inp.num("mortgageDebt")), (0));
            val autoDebt =J.orD((inp.num("autoLoanDebt")), (0));
            val studentCc =J.orD((inp.num("studentCreditDebt")), (0));

            val totalAssets = cash + retire + invest + reVal + carVal;
            val totalLiabilities = mort + autoDebt + studentCc;
            val netWorth = totalAssets - totalLiabilities;

            
            val liquidNetWorth = (cash + retire + invest) - (autoDebt + studentCc);
            val debtToAsset =(if (totalAssets > 0) (totalLiabilities / totalAssets) * 100 else 0.0);
            val homeEquity = J.max(0, reVal - mort);

            out["resTotalNetWorth"] = J.s("${'$'}" +J.locDf((J.round(netWorth))));
            out["resLiquidNetWorth"] = J.s("${'$'}" +J.locDf((J.round(liquidNetWorth))));
            out["resDebtToAssetRatio"] = J.s(J.toFixed((debtToAsset), (1).toInt()) + "%");
            out["resGrossAssets"] = J.s("${'$'}" +J.locDf((J.round(totalAssets))));
            out["resTotalLiabilities"] = J.s("-${'$'}" +J.locDf((J.round(totalLiabilities))));
            out["resHomeEquity"] = J.s("${'$'}" +J.locDf((J.round(homeEquity))));
            out["resSolvencyStatus"] = J.s((if (debtToAsset < 50) "Strong Financial Health (<50% leveraged)" else "Moderately Leveraged"));
        
  } catch (e: Exception) {
    
  }

    return out
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_hdhp_out_of_pocket_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val premium =J.orD((inp.num("annualPlanPremium")), (0));
            val ded =J.orD((inp.num("planDeductible")), (0));
            val coinsPct = (J.orD((inp.num("coinsurancePatientPct")), (20))) / 100;
            val oopMax =J.orD((inp.num("outOfPocketMaxLimit")), (6500));
            val claims =J.orD((inp.num("projectedMedicalClaims")), (0));

            val paidDed = J.min(claims, ded);
            val remainingClaims = J.max(0, claims - ded);
            val coinsPaidRaw = remainingClaims * coinsPct;
            val patientClaimsTotal = J.min(oopMax, paidDed + coinsPaidRaw);

            val insurerShare = J.max(0, claims - patientClaimsTotal);
            val allInCost = premium + patientClaimsTotal;
            val worstCaseAllIn = premium + oopMax;

            out["resPatientTotalPaid"] = J.s("${'$'}" +J.locDf((J.round(patientClaimsTotal))));
            out["resInsurerPaidAmount"] = J.s("${'$'}" +J.locDf((J.round(insurerShare))));
            out["resWorstCaseScenario"] = J.s("${'$'}" +J.locDf((J.round(worstCaseAllIn))) + " (Premium + OOP Max)");
            out["resDeductibleSpent"] = J.s("${'$'}" +J.locDf((J.round(paidDed))) + " (100% patient responsibility)");
            out["resCoinsuranceSpent"] = J.s("${'$'}" +J.locDf((J.round(patientClaimsTotal - paidDed))) + " (" +J.toFixed(((coinsPct * 100)), (0).toInt()) + "% coinsurance)");
            out["resAllInAnnual"] = J.s("${'$'}" +J.locDf((J.round(allInCost))) + " (Including annual premiums)");
            out["resAcaStatutoryLimit"] = J.s("2026 ACA statutory max: ${'$'}9,200 Individual / ${'$'}18,400 Family");
        
  } catch (e: Exception) {
    
  }

    return out
}

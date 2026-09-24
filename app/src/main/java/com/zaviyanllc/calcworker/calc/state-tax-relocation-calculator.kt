package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_state_tax_relocation_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    val gross =J.orD((inp.num("relocGross")), (135000));
    val status =J.orS((inp.str("relocStatus")), ("single"));
    val fromVal =J.orS((inp.str("stateFrom")), ("CA_0.093"));
    val toVal =J.orS((inp.str("stateTo")), ("TX_0.0"));
    val housingDelta =J.orD((inp.num("housingDiff")), (-600));

    val fromRate = state_tax_relocation_calculator_getEffectiveStateRate(fromVal, gross);
    val toRate = state_tax_relocation_calculator_getEffectiveStateRate(toVal, gross);

    var stdDed = 15000.0;
    if (status == "married") stdDed = 30000.0;
    else if (status == "hoh") stdDed = 22500.0;

    val taxableGross = J.max(0, gross - stdDed);

    val fromTax = taxableGross * fromRate;
    val toTax = taxableGross * toRate;

    val taxSavings = fromTax - toTax;
    val monthlyTaxSavings = taxSavings / 12;

    val annualHousingDiff = -(housingDelta * 12);
    val totalAnnualImpact = taxSavings + annualHousingDiff;
    val fiveYearImpact = totalAnnualImpact * 5;

    
    var fedTax = 0.0;
    if (taxableGross <= 48475) fedTax = taxableGross * 0.12;
    else if (taxableGross <= 103350) fedTax = 5578 + (taxableGross - 48475) * 0.22;
    else fedTax = 17651 + (taxableGross - 103350) * 0.24;
    val fica = J.min(gross, 176100) * 0.0765;
    val totalFedFica = fedTax + fica;

    val fromNet = gross - totalFedFica - fromTax - 36000;
    val toNet = gross - totalFedFica - toTax - (36000 + (housingDelta * 12));

    out["kpiAnnualTaxSavings"] = J.s(((if (taxSavings >= 0) "${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(taxSavings)))));
    out["kpiMonthlyGain"] = J.s(((if (monthlyTaxSavings >= 0) "${'$'}" else "-${'$'}")) +J.toFixed((kotlin.math.abs(monthlyTaxSavings)), (2).toInt()));
    out["kpiTotalAnnualImpact"] = J.s(((if (totalAnnualImpact >= 0) "${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(totalAnnualImpact)))));
    out["kpi5YearImpact"] = J.s(((if (fiveYearImpact >= 0) "${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(fiveYearImpact)))));

    out["thFrom"] = J.s(STATE_NAMES[fromVal.split("_")[0]] ?: "Current State");
    out["thTo"] = J.s(STATE_NAMES[toVal.split("_")[0]] ?: "Destination State");

    out["rowFromRate"] = J.s(J.toFixed(((fromRate * 100)), (1).toInt()) + "% eff.");
    out["rowToRate"] = J.s(J.toFixed(((toRate * 100)), (1).toInt()) + "% eff.");
    out["rowDiffRate"] = J.s(J.toFixed((((toRate - fromRate) * 100)), (1).toInt()) + "%");

    out["rowFromTax"] = J.s("${'$'}" +J.locDf((J.round(fromTax))));
    out["rowToTax"] = J.s("${'$'}" +J.locDf((J.round(toTax))));
    out["rowDiffTax"] = J.s(((if (taxSavings >= 0) "+${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(taxSavings)))));

    out["rowFedTax"] = J.s("${'$'}" +J.locDf((J.round(totalFedFica))));
    out["rowFedTaxTo"] = J.s("${'$'}" +J.locDf((J.round(totalFedFica))));

    out["rowToHousing"] = J.s("${'$'}" +J.locDf((J.round(36000 + (housingDelta * 12)))));
    out["rowDiffHousing"] = J.s(((if (annualHousingDiff >= 0) "+${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(annualHousingDiff)))));

    out["rowFromNet"] = J.s("${'$'}" +J.locDf((J.round(fromNet))));
    out["rowToNet"] = J.s("${'$'}" +J.locDf((J.round(toNet))));
    out["rowDiffNet"] = J.s(((if (totalAnnualImpact >= 0) "+${'$'}" else "-${'$'}")) +J.locDf((kotlin.math.abs(J.round(totalAnnualImpact)))) + "/yr");
  } catch (e: Exception) {
    
  }

    return out
}

  private val STATE_NAMES = mapOf(
    "AK" to "Alaska",
    "AL" to "Alabama",
    "AR" to "Arkansas",
    "AZ" to "Arizona",
    "CA" to "California",
    "CO" to "Colorado",
    "CT" to "Connecticut",
    "DC" to "District of Columbia / DC",
    "DE" to "Delaware",
    "FL" to "Florida",
    "GA" to "Georgia",
    "HI" to "Hawaii",
    "IA" to "Iowa",
    "ID" to "Idaho",
    "IL" to "Illinois",
    "IN" to "Indiana",
    "KS" to "Kansas",
    "KY" to "Kentucky",
    "LA" to "Louisiana",
    "MA" to "Massachusetts",
    "MD" to "Maryland",
    "ME" to "Maine",
    "MI" to "Michigan",
    "MN" to "Minnesota",
    "MO" to "Missouri",
    "MS" to "Mississippi",
    "MT" to "Montana",
    "NC" to "North Carolina",
    "ND" to "North Dakota",
    "NE" to "Nebraska",
    "NH" to "New Hampshire",
    "NJ" to "New Jersey",
    "NM" to "New Mexico",
    "NV" to "Nevada",
    "NY" to "New York",
    "OH" to "Ohio",
    "OK" to "Oklahoma",
    "OR" to "Oregon",
    "PA" to "Pennsylvania",
    "RI" to "Rhode Island",
    "SC" to "South Carolina",
    "SD" to "South Dakota",
    "TN" to "Tennessee",
    "TX" to "Texas",
    "UT" to "Utah",
    "VA" to "Virginia",
    "VT" to "Vermont",
    "WA" to "Washington",
    "WI" to "Wisconsin",
    "WV" to "West Virginia",
    "WY" to "Wyoming",
  )

private fun state_tax_relocation_calculator_getEffectiveStateRate(code: String, gross: Number): Double {
    var gross = gross.toDouble();
  val parts = code.split("_");
  val baseRate =J.orD(J.pf(parts[1]), 0.0);
  if (baseRate == 0.0) return 0.0;

  
  if (parts[0] == "CA") {
    if (gross <= 50000) return 0.04;
    if (gross <= 100000) return 0.065;
    if (gross <= 200000) return 0.082;
    return 0.098;
  }
  if (parts[0] == "NY") {
    if (gross <= 50000) return 0.045;
    if (gross <= 100000) return 0.059;
    if (gross <= 200000) return 0.068;
    return 0.085;
  }
  if (parts[0] == "NJ") {
    if (gross <= 70000) return 0.035;
    if (gross <= 150000) return 0.055;
    return 0.075;
  }
  return baseRate;

}

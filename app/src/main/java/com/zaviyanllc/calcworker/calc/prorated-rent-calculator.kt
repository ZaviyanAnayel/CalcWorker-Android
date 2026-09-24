package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_prorated_rent_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    val rent =J.orD((inp.num("rentInput")), (0));
    val moveIn =J.orD((J.piD(inp.str("moveInDay"), 10)), (1));
    val daysMonthVal = inp.str("daysInMonth");
    val daysInMonth =(if ((daysMonthVal == "365")) (365.0 / 12) else J.piD(daysMonthVal));

    val daysOccupied = J.max(0, kotlin.math.floor(daysInMonth) - moveIn + 1);
    val dailyRate = rent / daysInMonth;
    val prorated = dailyRate * daysOccupied;
    val saved = J.max(0, rent - prorated);

    out["proratedResult"] = J.s("${'$'}" +J.loc((prorated), (2).toInt(), (2).toInt()));
    out["daysOccupiedResult"] = J.s("(" + daysOccupied + " days occupied)");
    out["dailyRateResult"] = J.s("${'$'}" +J.toFixed((dailyRate), (2).toInt()) + " / day");
    out["savedResult"] = J.s("${'$'}" +J.loc((saved), (2).toInt(), (2).toInt()));
  
    return out
}

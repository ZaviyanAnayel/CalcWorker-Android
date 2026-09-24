package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_rent_vs_buy(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var price =J.orD((inp.num("buyPrice")), (0));
  var down =J.orD((inp.num("buyDown")), (0));
  var rate = (J.orD((inp.num("mortRate")), (0))) / 100;
  var rent =J.orD((inp.num("rentMonthly")), (0));
  var yrs =J.orD((J.piD(inp.str("stayYears"))), (5));

  var loan = J.max(0, price - down);
  var r = rate / 12;
  var n = 360.0;
  var pi =(if ((r > 0)) (loan * (r * J.pw(J.dbl(1+r), J.dbl(n))) / (J.pw(J.dbl(1+r), J.dbl(n)) - 1)) else (loan / n));
  var taxIns = (price * 0.015) / 12;
  var buyMonthly = pi + taxIns;

  var totalRent = rent * 12 * yrs * 1.05;
  var equityBuilt = (down + (price * 0.03 * yrs)) + (loan * 0.12 * (yrs / 5));
  var totalBuyOutflow = (buyMonthly * 12 * yrs) + down;
  var netBuyCost = totalBuyOutflow - equityBuilt;

  var diff = totalRent - netBuyCost;
  var breakeven = J.min(yrs, J.max(2.5, 4.5 * (buyMonthly / J.max(1, rent * 1.1))));

  if(diff >= 0){
    out["resWinner"] = J.s("Buying Wins");
    
    out["resWinnerDetail"] = J.s("+" + rent_vs_buy_fmt(diff) + " Net Wealth Advantage Over " + yrs + " Years");
  } else {
    out["resWinner"] = J.s("Renting Wins");
    
    out["resWinnerDetail"] = J.s("+" + rent_vs_buy_fmt(kotlin.math.abs(diff)) + " Net Savings by Renting Over " + yrs + " Years");
  }

  out["resBuyMonthly"] = J.s(rent_vs_buy_fmt(buyMonthly) + "/mo");
  out["resRentMonthly"] = J.s(rent_vs_buy_fmt(rent) + "/mo");
  out["resEquityBuilt"] = J.s("+" + rent_vs_buy_fmt(equityBuilt));
  out["resTotalRent"] = J.s(rent_vs_buy_fmt(totalRent));
  out["resBreakeven"] = J.s("~" +J.toFixed((breakeven), (1).toInt()) + " Years");

    return out
}

private fun rent_vs_buy_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.locDf((J.round(n)));
}

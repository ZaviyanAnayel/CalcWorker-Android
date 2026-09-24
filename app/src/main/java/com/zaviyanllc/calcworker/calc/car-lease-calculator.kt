package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_car_lease_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var msrp =J.orD((inp.num("leaseMsrp")), (0));
  var sell =J.orD((inp.num("leaseSell")), (0));
  var resPct = (J.orD((inp.num("leaseResPct")), (50))) / 100;
  var term =J.orD((J.piD(inp.str("leaseTerm"))), (36));
  var mf =J.orD((inp.num("moneyFactor")), (0.0025));
  var down =J.orD((inp.num("leaseDown")), (0));

  var residualVal = msrp * resPct;
  var netCapCost = J.max(0, sell - down);
  var totalDeprec = J.max(0, netCapCost - residualVal);
  var moDeprec = totalDeprec / term;
  var moRent = (netCapCost + residualVal) * mf;
  var moPayment = moDeprec + moRent;
  var totalCost = (moPayment * term) + down;

  out["resMonthlyLease"] = J.s(car_lease_calculator_fmt(moPayment) + "/mo");
  out["resLeaseSum"] = J.s(term + "-month term with " + car_lease_calculator_fmt(down) + " down");
  out["resMoDep"] = J.s(car_lease_calculator_fmt(moDeprec) + "/mo");
  out["resMoRent"] = J.s(car_lease_calculator_fmt(moRent) + "/mo");
  out["resResVal"] = J.s(car_lease_calculator_fmt(residualVal));
  out["resTotalOut"] = J.s(car_lease_calculator_fmt(totalCost));

    return out
}

private fun car_lease_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.locDf((J.round(n)));
}

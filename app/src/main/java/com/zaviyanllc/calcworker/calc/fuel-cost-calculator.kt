package com.zaviyanllc.calcworker.calc

import kotlin.math.*

private var fuelUnit: String = "us"

fun calc_fuel_cost_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  val dist =J.orD((inp.num("fuelDistance")), (0));
  val eff =J.orD((inp.num("fuelEfficiency")), (0));
  val price =J.orD((inp.num("fuelPrice")), (0));
  val passengers = J.max(1,J.orD((J.piD(inp.str("fuelPassengers"))), (1)));

  if (J.orD((dist <= 0), (eff <= 0 || price <= 0))) return out;

  var fuelQuantity = 0.0;
  var unitSymbol =(if (fuelUnit == "us") "gal" else "L");
  var distSymbol =(if (fuelUnit == "us") "mi" else "km");

  if (fuelUnit == "us") {
    fuelQuantity = dist / eff;
  } else {
    fuelQuantity = (dist * eff) / 100;
  }

  val totalCost = fuelQuantity * price;
  val costPerPerson = totalCost / passengers;
  val costPerUnit = totalCost / dist;

  out["resTotalFuelCost"] = J.s("${'$'}" +J.toFixed((totalCost), (2).toInt()));
  out["resPerPassenger"] = J.s("Each Passenger (${passengers} people): ${'$'}${J.toFixed((costPerPerson), (2).toInt())}");
  out["kpiFuelNeeded"] = J.s("${J.toFixed((fuelQuantity), (1).toInt())} ${unitSymbol}");
  out["lblKpiFuel"] = J.s("Total Fuel (${unitSymbol})");
  out["kpiCostPerDist"] = J.s("${'$'}${J.toFixed((costPerUnit), (2).toInt())} / ${distSymbol}");
  out["lblKpiDist"] = J.s("Cost per ${distSymbol}");

    return out
}

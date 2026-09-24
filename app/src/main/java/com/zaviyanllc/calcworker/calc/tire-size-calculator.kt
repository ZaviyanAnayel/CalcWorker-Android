package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_tire_size_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val sw =J.orD((inp.num("stockWidth")), (225));
            val sa =J.orD((inp.num("stockAspect")), (60));
            val sr =J.orD((inp.num("stockRim")), (17));

            val nw =J.orD((inp.num("newWidth")), (245));
            val na =J.orD((inp.num("newAspect")), (50));
            val nr =J.orD((inp.num("newRim")), (19));

            val speed =J.orD((inp.num("indicatedSpeed")), (65));

            
            val stockDia = sr + (2 * ((sw * (sa / 100)) / 25.4));
            val newDia = nr + (2 * ((nw * (na / 100)) / 25.4));

            val diaDiff = newDia - stockDia;
            val rideHeight = diaDiff / 2;

            val speedErrorPct =(if (stockDia > 0) ((newDia - stockDia) / stockDia) * 100 else 0.0);
            val actualSpeed = speed * (1 + (speedErrorPct / 100));

            val stockRev = (63360 / (stockDia * kotlin.math.PI));
            val newRev = (63360 / (newDia * kotlin.math.PI));
            val revDiff = newRev - stockRev;

            out["resActualSpeed"] = J.s(J.toFixed((actualSpeed), (1).toInt()) + " MPH");
            out["resSpeedError"] = J.s(((if (speedErrorPct >= 0) "+" else "")) +J.toFixed((speedErrorPct), (2).toInt()) + "%");
            out["resDiaDifference"] = J.s(((if (diaDiff >= 0) "+" else "")) +J.toFixed((diaDiff), (2).toInt()) + " in");

            out["resStockDia"] = J.s(J.toFixed((stockDia), (2).toInt()) + " in");
            out["resNewDia"] = J.s(J.toFixed((newDia), (2).toInt()) + " in");
            out["resRideHeightChange"] = J.s(((if (rideHeight >= 0) "+" else "")) +J.toFixed((rideHeight), (2).toInt()) + " in");
            out["resRevDifference"] = J.s(((if (revDiff >= 0) "+" else "")) + J.round(revDiff) + " revs/mi");out["resSummary"] = J.s("Tire Fitment: Stock Diameter ${J.toFixed((stockDia), (2).toInt())}\" vs New ${J.toFixed((newDia), (2).toInt())}\" (${((if (diaDiff>=0) "+" else ""))}${J.toFixed((diaDiff), (2).toInt())}\"). Speedometer error is ${J.toFixed((speedErrorPct), (2).toInt())}%. When reading ${speed} MPH, you are actually traveling at ${J.toFixed((actualSpeed), (1).toInt())} MPH."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}

package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_sales_tax_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  var sub =J.orD((inp.num("subtotal")), (0));
  var sel = inp.str("stateSelect");
  
  var rate = 0.0;

  if(sel == "custom"){
    
    rate = (J.orD((inp.num("customRate")), (0))) / 100;
  } else {
    
    rate =J.orD((J.pf(sel)), (0));
  }

  var tax = sub * rate;
  var total = sub + tax;

  out["resTotalPrice"] = J.s(sales_tax_calculator_fmt(total));
  out["resTaxDetail"] = J.s("Sales Tax: +" + sales_tax_calculator_fmt(tax) + " @ " +J.toFixed(((rate*100)), (2).toInt()) + "%");
  out["resSub"] = J.s(sales_tax_calculator_fmt(sub));
  out["resRateVal"] = J.s(J.toFixed(((rate*100)), (2).toInt()) + "%");
  out["resTaxCollected"] = J.s("+" + sales_tax_calculator_fmt(tax));
  out["resGrandTotal"] = J.s(sales_tax_calculator_fmt(total));

    return out
}

private fun sales_tax_calculator_fmt(n: Number): String {
    var n = n.toDouble();return "${'$'}"+J.loc((n), (2).toInt(), (2).toInt());
}

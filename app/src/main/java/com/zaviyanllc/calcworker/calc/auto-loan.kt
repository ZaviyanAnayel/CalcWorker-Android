package com.zaviyanllc.calcworker.calc

import kotlin.math.*

private var loanMonths: Double = 60.0
private var term: Double = loanMonths

fun calc_auto_loan(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  val price =J.orD((inp.num("carPrice")), (0));
  val down =J.orD((inp.num("downPayment")), (0));
  val trade =J.orD((inp.num("tradeIn")), (0));
  val apr =J.orD((inp.num("interestRate")), (0));
  val taxPct =J.orD((inp.num("salesTax")), (0));

  val taxableBase = J.max(0, price - trade);
  val taxAmount = taxableBase * (taxPct / 100);
  val loanAmount = J.max(0, (price - trade - down) + taxAmount);

  val r = (apr / 100) / 12;
  val n = loanMonths;
  var monthlyPayment = 0.0;

  if (loanAmount > 0) {
    if (r > 0) {
      monthlyPayment = loanAmount * (r * J.pw(J.dbl(1 + r), J.dbl(n))) / (J.pw(J.dbl(1 + r), J.dbl(n)) - 1);
    } else {
      monthlyPayment = loanAmount / n;
    }
  }

  val totalPaid = monthlyPayment * n;
  val totalInterest = J.max(0, totalPaid - loanAmount);
  val totalCost = price + taxAmount + totalInterest;

  out["resMonthlyPayment"] = J.s(auto_loan_fmtD(monthlyPayment));
  out["resTermSummary"] = J.s("${loanMonths}-month term @ ${J.toFixed((apr), (2).toInt())}% APR");
  out["resCarPrice"] = J.s(auto_loan_fmtD(price));
  out["resDownVal"] = J.s("-${auto_loan_fmtD(down)}");
  out["resTradeVal"] = J.s("-${auto_loan_fmtD(trade)}");
  out["resTaxVal"] = J.s("+${auto_loan_fmtD(taxAmount)}");
  out["resLoanAmount"] = J.s(auto_loan_fmtD(loanAmount));
  out["resTotalInterest"] = J.s(auto_loan_fmtD(totalInterest));
  out["resTotalCost"] = J.s(auto_loan_fmtD(totalCost));

    return out
}

private fun auto_loan_fmtD(n: Number): String {
    var n = n.toDouble();return "${'$'}"+auto_loan_fmt(n);
}

private fun auto_loan_fmt(n: Number): String {
    var n = n.toDouble();return J.loc((n), (0).toInt(), (0).toInt());
}

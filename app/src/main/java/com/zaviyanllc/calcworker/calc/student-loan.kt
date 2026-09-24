package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_student_loan(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

      val p = Inp(mapOf("balance" to J.dbl(inp.num("loanBalance")), "apr" to J.dbl(inp.num("loanApr")), "agi" to J.dbl(inp.num("borrowerAgi")), "familySize" to J.dbl(J.piD(inp.str("familySize"), 10)), "extraMonthly" to J.dbl(inp.num("extraLoanPayment"))))

      val res = CalcLib.calcStudentLoan(p);
      out["resStdMonthly"] = J.s("${'$'}${J.locDf((res.num("stdMonthly")))} / mo");
      out["resStdInterest"] = J.s("Total Interest Paid: ${'$'}${J.locDf((res.num("stdTotalInterest")))}");
      out["resSaveMonthly"] = J.s("${'$'}${J.locDf((res.num("saveMonthly")))} / mo");
      out["resAccTime"] = J.s("${res.num("accMonths")} Months (~${J.toFixed(((res.num("accMonths")/12)), (1).toInt())} Yrs)");
      out["resInterestSaved"] = J.s("${'$'}${J.locDf((res.num("interestSavedWithExtra")))}");
      out["resThreshold"] = J.s("${'$'}${J.locDf((res.num("povertyThreshold")))}");
    
    return out
}

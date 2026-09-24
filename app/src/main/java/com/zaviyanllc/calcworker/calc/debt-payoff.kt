package com.zaviyanllc.calcworker.calc

/** Hand port of debt-payoff (transpiler OVERRIDES: no generated file).
 *  Same contract as generated files: fun calc_<slug>(inp: Inp): LinkedHashMap<String, String>.
 *  The site's debt list is dynamic; here debts arrive via the optional "debts"
 *  input — one debt per line as "balance,apr,minPayment"
 *  (e.g. "5000,19.99,150\n2500,24.99,75"). Empty list yields zeroed outputs. */
fun calc_debt_payoff(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val extraMonthly = inp.num("extraMonthly")
    val debts = inp.str("debts").lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapNotNull { line ->
            val p = line.split(",")
            val bal = p.getOrNull(0)?.trim()?.toDoubleOrNull() ?: 0.0
            val apr = p.getOrNull(1)?.trim()?.toDoubleOrNull() ?: 0.0
            val min = p.getOrNull(2)?.trim()?.toDoubleOrNull() ?: 0.0
            if (bal > 0) CalcLib.Debt(bal, apr, min) else null
        }

    val res = CalcLib.calcDebtPayoff(debts, extraMonthly)
    val snowMo = res.num("snowballMonths").toLong()
    val avaMo = res.num("avalancheMonths").toLong()
    val snowInt = res.num("snowballInterest")
    val avaInt = res.num("avalancheInterest")
    val saved = res.num("interestSavedByAvalanche")
    val totalPrincipal = debts.sumOf { it.balance }
    val totalMin = debts.sumOf { it.minPayment }

    out["resSnowMonths"] = "$snowMo Mo"
    out["resSnowInterest"] = "$" + J.loc(snowInt, 0, 0)
    out["resAvaMonths"] = "$avaMo Mo"
    out["resAvaInterest"] = "$" + J.loc(avaInt, 0, 0)
    out["resInterestSaved"] = "$" + J.loc(saved, 0, 0)
    val moDiff = snowMo - avaMo
    out["resTimeSaved"] = if (moDiff > 0)
        "Plus debt-free $moDiff month" + (if (moDiff > 1) "s" else "") + " sooner"
    else
        "Both methods reach freedom in $avaMo months"
    out["resTotalPrincipal"] = "$" + J.loc(totalPrincipal, 0, 0)
    out["resTotalMonthly"] = "$" + J.loc(totalMin + extraMonthly, 0, 0) + " / mo"
    return out
}

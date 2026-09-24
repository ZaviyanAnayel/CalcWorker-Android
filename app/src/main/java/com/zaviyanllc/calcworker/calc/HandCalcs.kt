package com.zaviyanllc.calcworker.calc

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

/** Hand-written ports of high-value calculators the transpiler never generated.
 *  Same contract as generated files: fun calc_<slug>(inp: Inp): LinkedHashMap<String, String>. */

private fun money0(x: Double): String = "$" + J.loc(x, 0, 0)
private fun money2(x: Double): String = "$" + J.loc(x, 2, 2)

fun calc_mortgage_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val hp = inp.num("homePrice")
    val dp = inp.num("downDollar")
    val rate = inp.num("interestRate")
    val tax = inp.num("propTax")
    val ins = inp.num("insurance")
    val years = inp.int("loanTerm").let { if (it <= 0) 30 else it }
    val loan = max(0.0, hp - dp)
    val ltv = if (hp > 0) loan / hp * 100 else 0.0
    val pmi = if (ltv > 80) loan * 0.005 / 12 else 0.0
    val n = years * 12
    val r = rate / 100 / 12
    val pi = if (r > 0) loan * (r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1) else if (n > 0) loan / n else 0.0
    val taxMo = tax / 12
    val insMo = ins / 12
    val piti = pi + taxMo + insMo + pmi
    val totalPaid = pi * n
    val totalInt = max(0.0, totalPaid - loan)
    out["resPITI"] = money0(piti)
    out["resPandI"] = money0(pi)
    out["resTax"] = money0(taxMo)
    out["resIns"] = money0(insMo)
    out["resPMI"] = money0(pmi)
    out["resLoanAmt"] = money0(loan)
    out["resLTV"] = J.toFixed(ltv, 1) + "%"
    out["resTotalInt"] = money0(totalInt)
    out["resTotalCost"] = money0(max(0.0, totalPaid + dp))
    return out
}

fun calc_compound_interest(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val r = CalcLib.calcCompoundInterest(inp)
    val fv = J.dbl(r.summary.vals["futureValue"])
    val dep = J.dbl(r.summary.vals["totalDeposits"])
    val int = J.dbl(r.summary.vals["totalInterest"])
    out["resFutureValue"] = money0(fv)
    out["resTotalDeposits"] = money0(dep)
    out["resTotalInterest"] = money0(int)
    out["resRatio"] = if (fv > 0) J.toFixed(int / fv * 100, 1) + "% interest" else "—"
    out["resSubMetric"] = if (dep > 0) J.toFixed(fv / dep, 2) + "× your deposits" else "—"
    return out
}

fun calc_paycheck_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val g = inp.num("grossPay")
    val h = inp.num("hoursPerWeek").let { if (it == 0.0) 40.0 else it }
    val f = inp.str("payPeriod").ifEmpty { "hourly" }
    val ppy = when (f) {
        "daily" -> 260; "weekly" -> 52; "biweekly" -> 26
        "semimonthly" -> 24; "monthly" -> 12; else -> 52
    }
    val status = inp.str("filingStatus").ifEmpty { "single" }
    val sr = inp.num("stateSelect")
    val k = inp.num("k401") / 100
    val hi = inp.num("healthIns")
    val hs = inp.num("hsa")
    val aw = inp.num("addlWith")
    val ag = if (f == "hourly") g * h * 52 else g * ppy
    val gpp = ag / ppy
    val k4a = ag * k
    val hia = hi * 12
    val hsaa = hs * 12
    val ptA = k4a + hia + hsaa
    val ptPP = ptA / ppy
    val taxable = max(0.0, ag - ptA - (mapOf("single" to 16100.0, "married" to 32200.0, "marriedSep" to 16100.0, "hoh" to 24150.0)[status] ?: 16100.0))
    val fa = CalcLib.federalTax(taxable, status)
    val fpp = fa / ppy
    val ssa = min(ag, 184500.0) * 0.062
    val sspp = ssa / ppy
    val ma = ag * 0.0145 + max(0.0, ag - 200000) * 0.009
    val mpp = ma / ppy
    val sta = max(0.0, ag - ptA) * sr
    val stpp = sta / ppy
    val tded = fpp + sspp + mpp + stpp + ptPP + aw
    val net = gpp - tded
    val eff = if (ag > 0) (fa + ssa + ma + sta) / ag * 100 else 0.0
    out["resNetPay"] = money2(net)
    out["resAnnualNet"] = "Annual net take-home: " + money0(net * ppy)
    out["resGrossPP"] = money2(gpp)
    out["resAnnGross"] = money0(ag)
    out["resFedTax"] = "-" + money2(fpp)
    out["resSS"] = "-" + money2(sspp)
    out["resMedicare"] = "-" + money2(mpp)
    out["resStateTax"] = "-" + money2(stpp)
    out["resPreTax"] = "-" + money2(ptPP)
    out["resAddl"] = "-" + money2(aw)
    out["resTotalDed"] = "-" + money2(tded)
    out["resEffRate"] = J.toFixed(eff, 1) + "%"
    out["resNetFinal"] = money2(net)
    return out
}

private fun parseDate(s: String): LocalDate? = try {
    LocalDate.parse(s.trim(), DateTimeFormatter.ISO_LOCAL_DATE)
} catch (_: Exception) { null }

fun calc_age_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val dob = parseDate(inp.str("dobInput"))
    val target = parseDate(inp.str("targetDateInput")) ?: LocalDate.now()
    if (dob == null || target.isBefore(dob)) {
        out["resAgePrimary"] = "Enter a valid date of birth"
        return out
    }
    val p = Period.between(dob, target)
    val totalDays = ChronoUnit.DAYS.between(dob, target)
    val totalMonths = p.years * 12 + p.months
    out["resAgePrimary"] = "${p.years} years, ${p.months} months, ${p.days} days"
    out["resAgeDetailed"] = "${p.years}y ${p.months}m ${p.days}d old"
    out["statMonths"] = "$totalMonths months"
    out["statWeeks"] = "${totalDays / 7} weeks"
    out["statDays"] = "$totalDays days"
    out["statHours"] = "${totalDays * 24} hours"
    out["resBornWeekday"] = dob.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() } +
        ", " + dob.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    var next = dob.withYear(target.year)
    if (!next.isAfter(target)) next = next.plusYears(1)
    val untilNext = ChronoUnit.DAYS.between(target, next)
    out["resNextBday"] = "In $untilNext days — " + next.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy"))
    return out
}

fun calc_date_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val fmt = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")
    // add/subtract mode
    val start = parseDate(inp.str("startDateInput"))
    if (start != null) {
        val n = inp.num("numDaysInput").toLong()
        val res = if (inp.str("opSelect") == "sub") start.minusDays(n) else start.plusDays(n)
        out["resTargetDate"] = res.format(fmt)
        out["resDayOfWeek"] = res.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        out["statTotalDays"] = "$n days"
        out["statWeeks"] = J.toFixed(n / 7.0, 1) + " weeks"
        out["statHours"] = "${n * 24} hours"
        // business days in that span
        var biz = 0L
        var d = if (n >= 0) start else res
        val end = if (n >= 0) res else start
        while (!d.isAfter(end)) {
            val dow = d.dayOfWeek.value
            if (dow <= 5) biz++
            d = d.plusDays(1)
        }
        out["statBizDays"] = "$biz business days"
    }
    // between mode
    val b1 = parseDate(inp.str("betweenStart"))
    val b2 = parseDate(inp.str("betweenEnd"))
    if (b1 != null && b2 != null) {
        val (s, e) = if (b1.isAfter(b2)) b2 to b1 else b1 to b2
        val days = ChronoUnit.DAYS.between(s, e)
        val p = Period.between(s, e)
        out["resBetweenDays"] = "$days days"
        out["resBetweenBreakdown"] = "${p.years}y ${p.months}m ${p.days}d"
        out["statBetweenWeeks"] = J.toFixed(days / 7.0, 1) + " weeks"
        out["statBetweenHours"] = "${days * 24} hours"
        out["statBetweenMinutes"] = "${days * 24 * 60} minutes"
        var biz = 0L
        var d = s
        while (!d.isAfter(e)) {
            if (d.dayOfWeek.value <= 5) biz++
            d = d.plusDays(1)
        }
        out["statBetweenBiz"] = "$biz business days"
    }
    return out
}

fun calc_credit_card_payoff(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val bal0 = inp.num("ccBalance")
    val apr = inp.num("ccApr") / 100 / 12
    val pay = inp.num("ccMonthlyPay") + inp.num("ccExtra")
    fun simulate(balance: Double, payment: Double, minMode: Boolean): Triple<Int, Double, Double> {
        var b = balance
        var months = 0
        var interest = 0.0
        while (b > 0.005 && months < 600) {
            val i = b * apr
            interest += i
            b += i
            val p = if (minMode) max(25.0, b * 0.02) else payment
            if (p <= i && !minMode) return Triple(-1, 0.0, 0.0) // never pays off
            b -= min(p, b)
            months++
        }
        return Triple(months, interest, balance + interest)
    }
    val (months, interest, total) = simulate(bal0, pay, false)
    if (months < 0) {
        out["resMonths"] = "Never — payment doesn't cover interest"
        return out
    }
    val payoffDate = LocalDate.now().plusMonths(months.toLong())
        .format(DateTimeFormatter.ofPattern("MMM yyyy"))
    out["resMonths"] = "$months months"
    out["resPayoffDate"] = payoffDate
    out["resStartBal"] = money2(bal0)
    out["resTotalMoPay"] = money2(pay)
    out["resTotalInterest"] = money2(interest)
    out["resTotalPaid"] = money2(total)
    val (_, minInterest, _) = simulate(bal0, 0.0, true)
    out["resInterestSaved"] = money2(max(0.0, minInterest - interest))
    return out
}

fun calc_401k_rmd_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val bal = inp.num("accountBalance")
    val age = inp.int("ownerAge")
    val bracket = inp.num("estTaxBracket") / 100
    val table = mapOf(
        73 to 26.5, 74 to 25.5, 75 to 24.6, 76 to 23.7, 77 to 22.9, 78 to 22.0,
        79 to 21.1, 80 to 20.2, 81 to 19.4, 82 to 18.5, 83 to 17.7, 84 to 16.8,
        85 to 16.0, 86 to 15.2, 87 to 14.4, 88 to 13.7, 89 to 12.9, 90 to 12.2,
        91 to 11.5, 92 to 10.8, 93 to 10.1
    )
    val factor = table[age] ?: if (age > 93) 9.5 else 0.0
    val rmd = if (factor > 0) bal / factor else 0.0
    val tax = rmd * bracket
    val pct = if (bal > 0) rmd / bal * 100 else 0.0
    out["resAnnualRmd"] = if (factor > 0) money0(rmd) else "$0 (Under age 73)"
    out["resMonthlyRmd"] = if (factor > 0) money0(rmd / 12) + " / mo" else "$0"
    out["resTaxDue"] = money0(tax)
    out["resIrsFactor"] = if (factor > 0) J.toFixed(factor, 1) + " years" else "N/A (Age < 73)"
    out["resRmdPercent"] = J.toFixed(pct, 2) + "%"
    out["resAfterTaxRmd"] = money0(rmd - tax)
    out["resPenaltyNotice"] = if (factor > 0)
        "25% excise tax (10% if corrected within 2 yrs)" else "No RMD required until age 73"
    return out
}

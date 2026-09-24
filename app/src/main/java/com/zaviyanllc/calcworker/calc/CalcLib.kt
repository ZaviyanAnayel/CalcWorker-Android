package com.zaviyanllc.calcworker.calc

import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * CalcLib — 1:1 Kotlin port of the site's js/tools.js shared engine.
 * Pure functions, zero Android dependencies. 2026 IRS parameters (Rev. Proc. 2025-32):
 * standard deductions single $16,100 / MFJ $32,200 / HoH $24,150; SS wage base $184,500.
 */
object CalcLib {

    private val STD_DED = mapOf(
        "single" to 16100.0, "married" to 32200.0,
        "hoh" to 24150.0, "marriedSep" to 16100.0
    )

    // [upper bound, rate]
    private val BRACKETS = mapOf(
        "single" to listOf(
            12400.0 to .10, 50400.0 to .12, 105700.0 to .22, 201775.0 to .24,
            256225.0 to .32, 640600.0 to .35, Double.POSITIVE_INFINITY to .37
        ),
        "married" to listOf(
            24800.0 to .10, 100800.0 to .12, 211400.0 to .22, 403550.0 to .24,
            512450.0 to .32, 768700.0 to .35, Double.POSITIVE_INFINITY to .37
        ),
        "hoh" to listOf(
            17700.0 to .10, 67450.0 to .12, 105700.0 to .22, 201750.0 to .24,
            256200.0 to .32, 640600.0 to .35, Double.POSITIVE_INFINITY to .37
        ),
        "marriedSep" to listOf(
            12400.0 to .10, 50400.0 to .12, 105700.0 to .22, 201775.0 to .24,
            256225.0 to .32, 384350.0 to .35, Double.POSITIVE_INFINITY to .37
        )
    )

    private const val SS_WAGE_BASE_2026 = 184500.0

    private val STATE_RATES = mapOf(
        "TX" to 0.0, "FL" to 0.0, "WA" to 0.0, "standard" to 0.045,
        "PA" to 0.0307, "NC" to 0.0475, "CA" to 0.065, "NY" to 0.065
    )

    fun federalTax(taxable: Double, status: String): Double {
        val br = BRACKETS[status] ?: BRACKETS["single"]!!
        var tax = 0.0
        var prev = 0.0
        for ((upper, rate) in br) {
            if (taxable > prev) {
                tax += (min(taxable, upper) - prev) * rate
                prev = upper
            } else break
        }
        return tax
    }

    fun calcBreakEven(p: Inp): Inp {
        val fixed = p.num("fixedCosts")
        val vc = p.num("variableCost")
        val price = p.num("unitPrice")
        val target = p.num("targetProfit")
        val cm = price - vc
        val cmRatio = if (price > 0) (cm / price) * 100 else 0.0
        val beUnits = if (cm > 0) fixed / cm else 0.0
        val targetUnits = if (cm > 0) (fixed + target) / cm else 0.0
        return Inp(mapOf(
            "breakEvenUnits" to ceil(beUnits).toString(),
            "breakEvenRevenue" to (beUnits * price).toString(),
            "contributionMargin" to cm.toString(),
            "contributionMarginRatio" to (kotlin.math.round(cmRatio * 10) / 10).toString(),
            "targetUnits" to ceil(targetUnits).toString(),
            "targetRevenue" to (targetUnits * price).toString()
        ))
    }

    data class AmortYear(val year: Int, val deposits: Long, val interest: Long, val balance: Long)

    data class CompoundResult(val summary: Inp, val schedule: List<AmortYear>)

    fun calcCompoundInterest(p: Inp): CompoundResult {
        val principal = p.num("principal")
        val dep = p.num("monthlyDeposit")
        val apr = p.num("annualRate")
        val years = max(0, p.num("years").toInt())
        val r = apr / 100 / 12
        var bal = principal
        val schedule = mutableListOf<AmortYear>()
        var totalDep = principal
        for (y in 1..years) {
            var yrDep = 0.0
            var yrInt = 0.0
            for (m in 0 until 12) {
                val interest = bal * r
                bal += interest + dep
                yrInt += interest
                yrDep += dep
            }
            totalDep += yrDep
            schedule.add(AmortYear(y, totalDep.toLong(), (bal - totalDep).toLong(), bal.toLong()))
        }
        val fv = if (years == 0) principal else bal
        return CompoundResult(
            Inp(mapOf(
                "futureValue" to fv.toLong().toString(),
                "totalInterest" to (fv - totalDep).toLong().toString(),
                "totalDeposits" to totalDep.toLong().toString()
            )),
            schedule
        )
    }

    data class Debt(val balance: Double, val apr: Double, val minPayment: Double)

    fun calcDebtPayoff(debts: List<Debt>, extraMonthly: Double): Inp {
        fun simulate(order: List<Debt>): Pair<Int, Double> {
            val ds = order.map { doubleArrayOf(it.balance, it.apr, it.minPayment) }.toMutableList()
            val budget = ds.sumOf { it[2] } + extraMonthly
            var months = 0
            var interest = 0.0
            var guard = 0
            while (ds.any { it[0] > 0.005 } && guard < 1200) {
                guard++
                var totalMin = 0.0
                for (d in ds) {
                    if (d[0] <= 0.005) continue
                    val i = d[0] * (d[1] / 100 / 12)
                    interest += i
                    d[0] += i
                    val pay = min(d[2], d[0])
                    d[0] -= pay
                    totalMin += pay
                }
                var extra = budget - totalMin
                for (d in ds) {
                    if (extra <= 0.005) break
                    if (d[0] <= 0.005) continue
                    val pay = min(extra, d[0])
                    d[0] -= pay
                    extra -= pay
                }
                months++
                if (budget <= ds.sumOf { it[0] * (it[1] / 100 / 12) }) break
            }
            return months to interest
        }
        val snow = simulate(debts.sortedBy { it.balance })
        val ava = simulate(debts.sortedByDescending { it.apr })
        return Inp(mapOf(
            "snowballMonths" to snow.first.toString(),
            "snowballInterest" to snow.second.toLong().toString(),
            "avalancheMonths" to ava.first.toString(),
            "avalancheInterest" to ava.second.toLong().toString(),
            "interestSavedByAvalanche" to max(0, snow.second.toLong() - ava.second.toLong()).toString()
        ))
    }

    fun calcGigProfit(p: Inp): Inp {
        val gross = p.num("grossWeekly")
        val miles = p.num("milesWeekly")
        val hrs = p.num("hoursWeekly").let { if (it == 0.0) 1.0 else it }
        val gas = p.num("gasPrice")
        val mpg = p.num("mpg").let { if (it == 0.0) 1.0 else it }
        val maint = p.num("maintPerMile")
        val irsMile2026 = 0.725
        val gasCost = miles / mpg * gas
        val maintCost = miles * maint
        val net = gross - gasCost - maintCost
        val irsDed = miles * irsMile2026
        return Inp(mapOf(
            "netHourlyActual" to (net / hrs).toString(),
            "grossHourly" to (gross / hrs).toString(),
            "netWeekly" to net.toString(),
            "annualNet" to (net * 52).toString(),
            "irsDeductionWeekly" to irsDed.toString(),
            "gasCostWeekly" to gasCost.toString(),
            "maintCostWeekly" to maintCost.toString(),
            "taxableIncomeIRS" to max(0.0, gross - irsDed).toString()
        ))
    }

    fun calcHourlyRate(p: Inp): Inp {
        val desired = p.num("desiredNet")
        val exp = p.num("expenses")
        val hrsWk = p.num("billableHrsPerWk").let { if (it == 0.0) 1.0 else it }
        val wks = p.num("weeksWorked").let { if (it == 0.0) 1.0 else it }
        val taxR = p.num("taxRate") / 100
        val buffer = p.num("profitBuffer") / 100
        val billable = hrsWk * wks
        val netNeeded = (desired + exp) * (1 + buffer)
        val gross = if (taxR >= 1) netNeeded else netNeeded / (1 - taxR)
        val hourly = if (billable > 0) gross / billable else 0.0
        return Inp(mapOf(
            "hourlyRate" to (kotlin.math.round(hourly * 100) / 100).toString(),
            "totalBillableHours" to billable.toString(),
            "dayRate" to (hourly * 8).toString(),
            "weeklyRate" to (hourly * hrsWk).toString(),
            "annualGrossTarget" to gross.toString(),
            "monthlyGross" to (gross / 12).toString(),
            "totalTaxesEstimated" to (gross * taxR).toString()
        ))
    }

    fun calcRetirement(p: Inp): Inp {
        val years = max(0, (p.num("retirementAge") - p.num("currentAge")).toInt())
        val salary = p.num("salary")
        val contrib = p.num("contribPct") / 100
        val matchPct = p.num("matchPct") / 100
        val matchUpTo = p.num("matchUpTo") / 100
        val r = p.num("annualReturn") / 100 / 12
        var bal = p.num("currentSavings")
        val empAnnual = salary * contrib
        val empyrAnnual = min(empAnnual * matchPct, salary * matchUpTo)
        val monthly = (empAnnual + empyrAnnual) / 12
        for (m in 0 until years * 12) bal = bal * (1 + r) + monthly
        val totalContrib = (empAnnual + empyrAnnual) * years
        return Inp(mapOf(
            "totalNestEgg" to bal.toLong().toString(),
            "safeMonthlyIncome" to (bal * 0.04 / 12).toLong().toString(),
            "totalEmployeeContributions" to (empAnnual * years).toLong().toString(),
            "totalEmployerMatch" to (empyrAnnual * years).toLong().toString(),
            "totalInterestEarned" to (bal - p.num("currentSavings") - totalContrib).toLong().toString(),
            "yearsToRetire" to years.toString()
        ))
    }

    fun calcSolarROI(p: Inp): Inp {
        val bill = p.num("monthlyBill")
        val tariff = p.num("tariffRate")
        val kw = p.num("systemSizeKW")
        val sun = p.num("sunHours")
        val costPerWatt = 2.75
        val itc2026 = 0.30
        val annualKwh = kw * sun * 365
        val annualSavings = min(annualKwh * tariff, bill * 12)
        val grossCost = kw * 1000 * costPerWatt
        val credit = grossCost * itc2026
        val netCost = grossCost - credit
        val payback = if (annualSavings > 0) netCost / annualSavings else 0.0
        val net25 = annualSavings * 25 - netCost
        return Inp(mapOf(
            "net25YrSavings" to net25.toLong().toString(),
            "paybackYears" to (kotlin.math.round(payback * 10) / 10).toString(),
            "federalTaxCredit" to credit.toLong().toString(),
            "netCost" to netCost.toLong().toString(),
            "annualKwh" to annualKwh.toLong().toString(),
            "annualSavings" to annualSavings.toLong().toString(),
            "roiPercent" to (if (netCost > 0) kotlin.math.round(net25 / netCost * 100).toLong() else 0).toString()
        ))
    }

    fun calcStudentLoan(p: Inp): Inp {
        val bal0 = p.num("balance")
        val apr = p.num("apr") / 100 / 12
        val extra = p.num("extraMonthly")
        val fam = max(1, (p.num("familySize").let { if (it == 0.0) 1.0 else it }).toInt())
        val poverty = 16100 + (fam - 1) * 5700
        val n = 120
        var stdMonthly = 0.0
        var stdInterest = 0.0
        if (bal0 > 0) {
            stdMonthly = if (apr > 0) bal0 * (apr * (1 + apr).pow(n)) / ((1 + apr).pow(n) - 1) else bal0 / n
            stdInterest = stdMonthly * n - bal0
        }
        var b = bal0
        var months = 0
        var intPaid = 0.0
        val pay = stdMonthly + extra
        while (b > 0.005 && months < 1200 && pay > 0) {
            val i = b * apr
            intPaid += i
            b += i
            b -= min(pay, b)
            months++
            if (pay <= b * apr) break
        }
        return Inp(mapOf(
            "stdMonthly" to (kotlin.math.round(stdMonthly * 100) / 100).toString(),
            "stdTotalInterest" to stdInterest.toLong().toString(),
            "saveMonthly" to (kotlin.math.round(extra * 100) / 100).toString(),
            "accMonths" to months.toString(),
            "interestSavedWithExtra" to max(0, (stdInterest - intPaid).toLong()).toString(),
            "povertyThreshold" to poverty.toString()
        ))
    }

    fun calcTaxWithholding(p: Inp): Inp {
        val gross = p.num("annualGross")
        val status = p.str("filingStatus").ifEmpty { "single" }
        val preTax = p.num("preTaxDeductions")
        val stdDed = STD_DED[status] ?: STD_DED["single"]!!
        val taxable = max(0.0, gross - preTax - stdDed)
        val fed = federalTax(taxable, status)
        val ss = min(gross, SS_WAGE_BASE_2026) * 0.062
        val med = gross * 0.0145
        val addlLimit = if (status == "married") 250000.0 else 200000.0
        val addl = max(0.0, gross - addlLimit) * 0.009
        val fica = ss + med + addl
        val stateRate = STATE_RATES[p.str("state")] ?: STATE_RATES["standard"]!!
        val state = max(0.0, gross - preTax) * stateRate
        val total = fed + fica + state
        val net = gross - total
        return Inp(mapOf(
            "netBiWeekly" to (net / 26).toLong().toString(),
            "netMonthly" to (net / 12).toLong().toString(),
            "netPayAnnual" to net.toLong().toString(),
            "effectiveRate" to (if (gross > 0) kotlin.math.round(total / gross * 1000) / 10 else 0.0).toString(),
            "fedTax" to fed.toLong().toString(),
            "ficaTax" to fica.toLong().toString(),
            "stateTax" to state.toLong().toString(),
            "totalTaxes" to total.toLong().toString()
        ))
    }
}

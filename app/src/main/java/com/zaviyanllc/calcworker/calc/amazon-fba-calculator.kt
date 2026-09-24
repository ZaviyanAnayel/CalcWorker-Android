package com.zaviyanllc.calcworker.calc

/** Hand port of amazon-fba-calculator (transpiler OVERRIDES: no generated file).
 *  Same contract as generated files: fun calc_<slug>(inp: Inp): LinkedHashMap<String, String>.
 *  Site default fulfillment method is FBA; pass method="fbm" to use FBM math. */
fun calc_amazon_fba_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val price = inp.num("sellingPrice")
    val cogs = inp.num("cogs")
    val freight = inp.num("inboundFreight")
    val refRate = J.orD(inp.num("category"), 0.15)
    val ppc = inp.num("ppcSpend")
    val returnRate = inp.num("returnRate")
    val method = inp.str("method").ifEmpty { "fba" }

    val referralFee = price * refRate

    val fulfillmentFee = if (method == "fba") {
        val weight = J.orD(inp.num("unitWeight"), 1.2)
        val inboundPlacement = J.orD(inp.num("inboundPlacement"), 0.27)
        val storageSeason = J.orD(inp.num("storageSeason"), 0.08)
        var basePickPack = 4.25
        if (price < 10.0) basePickPack = 3.48
        else if (weight > 2.0) basePickPack = 5.40
        basePickPack + inboundPlacement + storageSeason
    } else {
        val fbmShip = J.orD(inp.num("fbmShippingCost"), 5.80)
        val fbmPack = J.orD(inp.num("fbmPackaging"), 0.75)
        fbmShip + fbmPack
    }

    val returnCost = (referralFee * 0.20) * (returnRate / 100)
    val totalFees = referralFee + fulfillmentFee + returnCost
    val totalUnitCost = cogs + freight + ppc + totalFees
    val netProfit = price - totalUnitCost
    val netMargin = if (price > 0) netProfit / price * 100 else 0.0
    val roi = if (cogs + freight > 0) netProfit / (cogs + freight) * 100 else 0.0
    val breakEven = (cogs + freight + ppc + fulfillmentFee + returnCost) / (1 - refRate)

    out["marginSub"] = "Net Margin: ${J.toFixed(netMargin, 1)}% | ROI: ${J.toFixed(roi, 1)}%"
    out["kpiTotalFees"] = "$" + J.toFixed(totalFees, 2)
    out["kpiFulfillmentFee"] = "$" + J.toFixed(fulfillmentFee, 2)
    out["kpiReferralFee"] = "$" + J.toFixed(referralFee, 2)
    out["kpiBreakEven"] = "$" + J.toFixed(breakEven, 2)
    out["rowSellingPrice"] = "$" + J.toFixed(price, 2)
    out["rowCOGS"] = "-$" + J.toFixed(cogs, 2)
    out["rowFreight"] = "-$" + J.toFixed(freight, 2)
    out["rowReferral"] = "-$" + J.toFixed(referralFee, 2)
    out["rowFulfillment"] = "-$" + J.toFixed(fulfillmentFee, 2)
    out["rowPPC"] = "-$" + J.toFixed(ppc, 2)
    out["kpiNetProfit"] = "$" + J.toFixed(netProfit, 2)
    return out
}

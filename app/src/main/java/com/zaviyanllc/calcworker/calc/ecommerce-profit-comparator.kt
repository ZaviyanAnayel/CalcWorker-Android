package com.zaviyanllc.calcworker.calc

private data class EcomPlatform(val name: String, val profit: Double, val margin: Double, val fees: Double)

/** Hand port of ecommerce-profit-comparator (transpiler OVERRIDES: no generated file).
 *  Same contract as generated files: fun calc_<slug>(inp: Inp): LinkedHashMap<String, String>.
 *  Compares per-order profit across Shopify DTC, TikTok Shop, Etsy, Amazon, eBay
 *  using the site's 2026 fee assumptions; winner = highest profit. */
fun calc_ecommerce_profit_comparator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val price = inp.num("itemPrice")
    val shipCharged = inp.num("shippingCharged")
    val cogs = inp.num("itemCost")
    val shipCost = inp.num("shippingCost")
    val packaging = inp.num("packagingCost")
    val adSpend = inp.num("adSpend")
    val category = inp.str("categorySelect")
    val fulfillment = inp.str("amazonFulfillment")
    val totalRevenue = price + shipCharged

    // 1. Shopify DTC: 2.9% + $0.30 payment gateway
    val shopifyFees = if (totalRevenue > 0) totalRevenue * 0.029 + 0.30 else 0.0
    val shopifyCosts = cogs + shipCost + packaging + adSpend
    val shopifyProfit = totalRevenue - shopifyFees - shopifyCosts
    val shopifyMargin = if (totalRevenue > 0) shopifyProfit / totalRevenue * 100 else 0.0

    // 2. TikTok Shop: 6% referral + $0.30 processing (lower organic ad spend assumed)
    val tiktokRate = 0.06
    val tiktokFees = if (totalRevenue > 0) totalRevenue * tiktokRate + 0.30 else 0.0
    val tiktokCosts = cogs + shipCost + packaging + adSpend * 0.5
    val tiktokProfit = totalRevenue - tiktokFees - tiktokCosts
    val tiktokMargin = if (totalRevenue > 0) tiktokProfit / totalRevenue * 100 else 0.0

    // 3. Etsy: $0.20 listing + 6.5% transaction + 3% + $0.25 payment
    val etsyFees = 0.20 + totalRevenue * 0.065 + (if (totalRevenue > 0) totalRevenue * 0.03 + 0.25 else 0.0)
    val etsyCosts = cogs + shipCost + packaging
    val etsyProfit = totalRevenue - etsyFees - etsyCosts
    val etsyMargin = if (totalRevenue > 0) etsyProfit / totalRevenue * 100 else 0.0

    // 4. Amazon
    var amzReferralRate = 0.15
    if (category == "apparel") amzReferralRate = 0.17
    if (category == "electronics") amzReferralRate = 0.08
    var amzPostage = shipCost
    var amzFulfillmentFee = 0.0
    if (fulfillment == "fba") {
        amzPostage = 0.0
        amzFulfillmentFee = 4.35
    }
    val amzFees = totalRevenue * amzReferralRate + amzFulfillmentFee
    val amzCosts = cogs + amzPostage + packaging + adSpend * 0.4
    val amzProfit = totalRevenue - amzFees - amzCosts
    val amzMargin = if (totalRevenue > 0) amzProfit / totalRevenue * 100 else 0.0
    val amzName = if (fulfillment == "fba") "📦 Amazon FBA" else "📦 Amazon FBM"

    // 5. eBay: 13.25% + $0.40
    var ebayRate = 0.1325
    if (category == "electronics") ebayRate = 0.0935
    val ebayFees = if (totalRevenue > 0) totalRevenue * ebayRate + 0.40 else 0.0
    val ebayCosts = cogs + shipCost + packaging
    val ebayProfit = totalRevenue - ebayFees - ebayCosts
    val ebayMargin = if (totalRevenue > 0) ebayProfit / totalRevenue * 100 else 0.0

    val platforms = listOf(
        EcomPlatform("🛍️ Shopify DTC", shopifyProfit, shopifyMargin, shopifyFees),
        EcomPlatform("🎵 TikTok Shop", tiktokProfit, tiktokMargin, tiktokFees),
        EcomPlatform("🎨 Etsy Shop", etsyProfit, etsyMargin, etsyFees),
        EcomPlatform(amzName, amzProfit, amzMargin, amzFees),
        EcomPlatform("🏷️ eBay Store", ebayProfit, ebayMargin, ebayFees)
    )
    val winner = platforms.maxByOrNull { it.profit } ?: platforms[0]

    out["winnerPlatformTitle"] = "${winner.name} (\$${J.toFixed(winner.profit, 2)} Net)"
    out["winnerProfit"] = "$" + J.toFixed(winner.profit, 2)
    out["winnerPlatformSub"] = "Generates the highest margin at ${J.toFixed(winner.margin, 1)}% " +
        "with \$${J.toFixed(winner.fees, 2)} in total channel fees."
    return out
}

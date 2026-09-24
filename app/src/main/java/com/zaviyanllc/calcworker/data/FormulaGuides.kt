package com.zaviyanllc.calcworker.data

/** Step-by-step formula guides for the most-used calculators.
 *  Fully offline — powers the AI screen's "explain the formula" answers
 *  when there is no internet connection. */
object FormulaGuides {

    data class Guide(
        val slug: String,
        val title: String,
        val keywords: List<String>,
        val formula: String,
        val steps: List<String>,
        val example: String
    )

    val guides: List<Guide> = listOf(
        Guide(
            slug = "mortgage-calculator",
            title = "Mortgage Payment",
            keywords = listOf("mortgage", "home loan", "house payment", "monthly payment"),
            formula = "M = P × r(1+r)ⁿ ÷ ((1+r)ⁿ − 1)",
            steps = listOf(
                "Convert the annual rate to a monthly rate: r = annual rate ÷ 12.",
                "Count total payments: n = years × 12 (360 for a 30-year loan).",
                "Compute (1+r)ⁿ — the growth factor over the whole loan.",
                "Monthly payment M = P × r × (1+r)ⁿ ÷ ((1+r)ⁿ − 1), where P is the loan amount.",
                "Early payments are mostly interest; the principal share grows each month (amortization)."
            ),
            example = "Example: \$300,000 at 6.5% for 30 years → r = 0.00542, n = 360 → about \$1,896/month."
        ),
        Guide(
            slug = "compound-interest",
            title = "Compound Interest",
            keywords = listOf("compound interest", "compound", "growth", "savings growth", "interest on interest"),
            formula = "A = P(1 + r/n)^(n×t)",
            steps = listOf(
                "Start with principal P (your initial deposit).",
                "Divide the annual rate r by compounding frequency n (12 for monthly, 4 for quarterly, 1 for yearly).",
                "Raise (1 + r/n) to the power of total periods (n × t, where t = years).",
                "Multiply by P to get the final amount A. Interest earned = A − P.",
                "More frequent compounding and more time both accelerate growth — time matters most."
            ),
            example = "Example: \$10,000 at 7% compounded monthly for 10 years → about \$20,096."
        ),
        Guide(
            slug = "bmi-calculator",
            title = "Body Mass Index (BMI)",
            keywords = listOf("bmi", "body mass", "obesity", "weight status", "healthy weight"),
            formula = "BMI = weight(kg) ÷ height(m)²",
            steps = listOf(
                "Weigh yourself in kilograms (pounds ÷ 2.205).",
                "Measure height in meters (inches × 0.0254).",
                "Square the height, then divide weight by that number.",
                "Read the result: below 18.5 = underweight, 18.5–24.9 = healthy, 25–29.9 = overweight, 30+ = obese.",
                "BMI is a screening tool — athletes with high muscle may read high without excess fat."
            ),
            example = "Example: 70 kg ÷ (1.75 m)² = 70 ÷ 3.06 ≈ 22.9 → healthy range."
        ),
        Guide(
            slug = "tip-calculator",
            title = "Tip & Bill Split",
            keywords = listOf("tip", "gratuity", "split bill", "restaurant tip"),
            formula = "Tip = bill × tip%  •  Total = bill + tip",
            steps = listOf(
                "Take the pre-tax bill amount.",
                "Multiply by the tip rate (15% = 0.15, 20% = 0.20).",
                "Add the tip to the bill for the total.",
                "To split: divide the total by the number of people.",
                "Standard in the US: 15–20% for table service, more for exceptional service."
            ),
            example = "Example: \$50 bill at 20% → tip \$10, total \$60 → \$30 each for 2 people."
        ),
        Guide(
            slug = "percentage-calculator",
            title = "Percentages",
            keywords = listOf("percent", "percentage", "%", "percent change", "percent of"),
            formula = "X% of Y = (X ÷ 100) × Y",
            steps = listOf(
                "“X% of Y”: multiply Y by X ÷ 100.",
                "“X is what % of Y”: divide X by Y, then multiply by 100.",
                "“% increase/decrease”: (new − old) ÷ old × 100.",
                "To add X%: multiply by (1 + X/100). To subtract X%: multiply by (1 − X/100).",
                "Remember: a 50% increase followed by a 50% decrease does NOT return to the start."
            ),
            example = "Example: 18% of 450 = 0.18 × 450 = 81.  90 is what % of 450? 90 ÷ 450 × 100 = 20%."
        ),
        Guide(
            slug = "simple-interest-calculator",
            title = "Simple Interest",
            keywords = listOf("simple interest", "flat interest"),
            formula = "I = P × r × t",
            steps = listOf(
                "Take the principal P (amount borrowed or invested).",
                "Multiply by the annual rate r (as a decimal) and time t in years.",
                "Total owed/received = P + I.",
                "Unlike compound interest, the base never grows — interest is charged only on the original principal."
            ),
            example = "Example: \$5,000 at 5% for 3 years → I = 5000 × 0.05 × 3 = \$750."
        ),
        Guide(
            slug = "personal-loan-calculator",
            title = "Loan Payment (EMI)",
            keywords = listOf("loan", "emi", "personal loan", "monthly installment", "car payment", "amortization"),
            formula = "EMI = P × r(1+r)ⁿ ÷ ((1+r)ⁿ − 1)",
            steps = listOf(
                "Monthly rate r = annual rate ÷ 12; n = total monthly payments.",
                "Apply the amortization formula above with loan amount P.",
                "Each payment = interest on remaining balance + principal chunk.",
                "Total interest = (EMI × n) − P. Shorter terms save interest but raise the payment.",
                "Extra principal payments shorten the loan and cut total interest."
            ),
            example = "Example: \$20,000 at 8% for 5 years → about \$406/month, total interest ≈ \$4,332."
        ),
        Guide(
            slug = "auto-loan",
            title = "Auto Loan",
            keywords = listOf("auto loan", "car loan", "vehicle financing"),
            formula = "Payment = P × r(1+r)ⁿ ÷ ((1+r)ⁿ − 1), P = price − down payment − trade-in",
            steps = listOf(
                "Subtract down payment and trade-in from the car price to get amount financed P.",
                "Monthly rate r = APR ÷ 12; n = loan months (36, 48, 60, 72).",
                "Apply the amortization formula for the monthly payment.",
                "Compare total cost across terms — longer terms lower the payment but raise total interest.",
                "Watch for dealer add-ons rolled into the loan; they accrue interest too."
            ),
            example = "Example: \$28,000 car, \$4,000 down, 6% for 60 months → about \$464/month."
        ),
        Guide(
            slug = "age-calculator",
            title = "Exact Age",
            keywords = listOf("age", "how old", "birthday", "date of birth", "years months days"),
            formula = "Age = today − birth date (borrowing months/days as needed)",
            steps = listOf(
                "Subtract birth year from current year.",
                "If this year's birthday hasn't happened yet, subtract 1 year.",
                "Months = current month − birth month (borrow 12 if negative).",
                "Days = current day − birth day (borrow the previous month's day count if negative).",
                "Leap-day babies (Feb 29): age increments on Feb 28 or Mar 1 in non-leap years."
            ),
            example = "Example: born 15 Jun 1990, today 23 Sep 2026 → 36 years, 3 months, 8 days."
        ),
        Guide(
            slug = "sales-tax-calculator",
            title = "Sales Tax",
            keywords = listOf("sales tax", "tax on purchase", "vat", "with tax"),
            formula = "Tax = price × rate  •  Total = price + tax",
            steps = listOf(
                "Take the pre-tax price.",
                "Multiply by the local tax rate (8.5% = 0.085).",
                "Add the tax to the price for the checkout total.",
                "To find the pre-tax price from a total: price = total ÷ (1 + rate).",
                "Rates combine state + county + city — always use your full local rate."
            ),
            example = "Example: \$100 at 8.5% → tax \$8.50, total \$108.50."
        ),
        Guide(
            slug = "calorie-calculator",
            title = "Daily Calories (BMR)",
            keywords = listOf("calorie", "bmr", "tdee", "metabolism", "how many calories", "weight loss calories"),
            formula = "Mifflin-St Jeor: Men: 10w + 6.25h − 5a + 5  •  Women: 10w + 6.25h − 5a − 161",
            steps = listOf(
                "Compute BMR with weight w (kg), height h (cm), age a — the calories you burn at rest.",
                "Multiply by activity: 1.2 sedentary, 1.375 light, 1.55 moderate, 1.725 very active.",
                "Result = TDEE, your daily maintenance calories.",
                "To lose ~0.5 kg/week: eat about 500 below TDEE. To gain: 250–500 above.",
                "Recalculate every few months — BMR drops as weight drops."
            ),
            example = "Example: 30-yr-old man, 80 kg, 180 cm → BMR ≈ 1,780 → moderately active TDEE ≈ 2,759."
        ),
        Guide(
            slug = "paycheck-calculator",
            title = "Paycheck / Take-Home Pay",
            keywords = listOf("paycheck", "salary", "take home", "net pay", "after tax pay", "w2"),
            formula = "Net = gross − federal tax − state tax − FICA (7.65%) − deductions",
            steps = listOf(
                "Start with gross pay for the period.",
                "Subtract pre-tax deductions (401k, health premiums) to get taxable income.",
                "Apply federal income tax brackets and your state tax.",
                "Subtract FICA: 6.2% Social Security (up to the wage base) + 1.45% Medicare.",
                "Subtract post-tax deductions (Roth 401k, garnishments) → net take-home."
            ),
            example = "Example: \$5,000/month gross, single, no state tax → roughly \$3,850–\$4,000 take-home."
        ),
        Guide(
            slug = "apr-to-apy-calculator",
            title = "APR to APY",
            keywords = listOf("apr", "apy", "annual percentage", "effective rate", "true rate"),
            formula = "APY = (1 + APR/n)ⁿ − 1",
            steps = listOf(
                "Take the nominal APR as a decimal.",
                "Divide by n = compounding periods per year (12 monthly, 365 daily).",
                "Raise to the power n, then subtract 1.",
                "APY is the true yearly rate including compounding — always compare loans and savings with APY.",
                "More frequent compounding → higher APY for the same APR."
            ),
            example = "Example: 5% APR compounded monthly → APY = (1 + 0.05/12)¹² − 1 ≈ 5.116%."
        ),
        Guide(
            slug = "date-calculator",
            title = "Date Difference",
            keywords = listOf("date difference", "days between", "countdown", "how many days", "duration between dates"),
            formula = "Days = later date − earlier date (leap years counted)",
            steps = listOf(
                "Convert both dates to day counts (days since a fixed epoch).",
                "Subtract: later − earlier = total days.",
                "Break into years/months/days using real month lengths and leap years.",
                "Leap rule: divisible by 4, except centuries unless divisible by 400.",
                "Business days: subtract weekends (and optionally holidays) from the total."
            ),
            example = "Example: 1 Jan 2026 → 23 Sep 2026 = 265 days."
        ),
        Guide(
            slug = "currency-converter",
            title = "Currency Conversion",
            keywords = listOf("currency", "exchange", "forex", "convert money", "dollar to", "exchange rate"),
            formula = "Converted = amount × (rate_to ÷ rate_from), rates vs USD",
            steps = listOf(
                "All rates are stored against USD (Sep-2026 baseline baked in, refreshed live when online).",
                "Divide the target currency's rate by the source currency's rate.",
                "Multiply your amount by that ratio.",
                "Banks add 1–3% spread over the mid-market rate — the converter shows the fair mid-market value.",
                "When offline, the Sep-2026 baseline is used automatically."
            ),
            example = "Example: 100 USD → EUR at 0.86 → €86.00."
        ),
        Guide(
            slug = "ai-token-calculator",
            title = "AI Token Cost",
            keywords = listOf("ai token", "token cost", "llm cost", "openai cost", "api cost", "gpt cost", "claude cost"),
            formula = "Cost = (input tokens ÷ 1M × input price) + (output tokens ÷ 1M × output price)",
            steps = listOf(
                "Estimate input tokens: roughly 1 token ≈ ¾ of an English word.",
                "Look up the model's per-1M-token input and output prices.",
                "Cost = input tokens/1M × input price + output tokens/1M × output price.",
                "Output tokens usually cost 2–4× more than input tokens.",
                "Cut costs: shorten prompts, use smaller models for simple tasks, cache repeated context."
            ),
            example = "Example: 50K input + 10K output tokens at \$5/\$15 per 1M → \$0.25 + \$0.15 = \$0.40."
        )
    )

    /** Best guide for a question, or null. Matches on keywords + title. */
    fun match(query: String): Guide? {
        val q = query.lowercase()
        val words = q.split(Regex("[^a-z0-9]+")).filter { it.isNotEmpty() }.toSet()
        var best: Guide? = null
        var bestScore = 0
        for (g in guides) {
            var score = 0
            if (q.contains(g.title.lowercase())) score += 10
            for (kw in g.keywords) {
                val k = kw.lowercase()
                // whole-word hit (e.g. "bmi", "tip") scores highest; substring scores less
                if (words.contains(k)) score += 6
                else if (k.length > 4 && q.contains(k)) score += 3
                else if (q.contains(k)) score += 1
            }
            // "explain the X formula" style: title words present
            for (w in g.title.lowercase().split(" ")) {
                if (w.length > 3 && words.contains(w)) score += 1
            }
            if (score > bestScore) { bestScore = score; best = g }
        }
        return if (bestScore >= 4) best else null
    }

    /** Rendered step-by-step answer text. */
    fun render(g: Guide): String {
        val sb = StringBuilder()
        sb.append("${g.title} — how it works:\n\n")
        sb.append("Formula: ${g.formula}\n\n")
        sb.append("Steps:\n")
        g.steps.forEachIndexed { i, s -> sb.append("${i + 1}. $s\n") }
        sb.append("\n${g.example}")
        sb.append("\n\nOpen the ${g.title} calculator from the home screen to compute it instantly — works offline.")
        return sb.toString()
    }
}

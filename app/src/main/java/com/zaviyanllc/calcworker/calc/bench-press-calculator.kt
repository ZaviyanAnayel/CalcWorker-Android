package com.zaviyanllc.calcworker.calc

/** Hand port of bench-press-calculator (transpiler OVERRIDES: no generated file).
 *  Same contract as generated files: fun calc_<slug>(inp: Inp): LinkedHashMap<String, String>.
 *  Site default unit is lbs; the spec carries no unit input so lbs is used. */
fun calc_bench_press_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val w = inp.num("liftWeight")
    val r = inp.int("liftReps").let { if (it <= 0) 1 else it }
    val formula = inp.str("formulaSelect")

    out["repDisplay"] = "$r Rep" + (if (r > 1) "s" else "")
    if (w > 0) {
        var max = w
        if (r > 1) {
            val brzycki = w / (1.0278 - 0.0278 * r)
            val epley = w * (1 + r / 30.0)
            val lander = 100 * w / (101.3 - 2.67123 * r)
            max = when (formula) {
                "brzycki" -> brzycki
                "epley" -> epley
                "lander" -> lander
                else -> (brzycki + epley + lander) / 3
            }
        }
        val roundedMax = kotlin.math.round(max).toLong()
        out["resMaxBench"] = "$roundedMax lbs"
        out["resStrengthClass"] = when {
            roundedMax >= 315 -> "🏆 Elite Powerlifter Level (315+ lbs)"
            roundedMax >= 225 -> "💪 Advanced (2+ Plate Club, 225+ lbs)"
            roundedMax >= 175 -> "⭐ Intermediate Lifter (175+ lbs)"
            roundedMax >= 135 -> "🎯 Novice (1 Plate Club, 135+ lbs)"
            else -> "🌱 Developing Strength"
        }
    }
    return out
}

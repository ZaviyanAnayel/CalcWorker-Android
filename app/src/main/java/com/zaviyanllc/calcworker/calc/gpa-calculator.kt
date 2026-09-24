package com.zaviyanllc.calcworker.calc

private val GPA_POINTS = mapOf(
    "A+" to 4.0, "A" to 4.0, "A-" to 3.7,
    "B+" to 3.3, "B" to 3.0, "B-" to 2.7,
    "C+" to 2.3, "C" to 2.0, "C-" to 1.7,
    "D+" to 1.3, "D" to 1.0, "D-" to 0.7,
    "F" to 0.0
)

/** Hand port of gpa-calculator (transpiler OVERRIDES: no generated file).
 *  Same contract as generated files: fun calc_<slug>(inp: Inp): LinkedHashMap<String, String>.
 *  The site's course list is dynamic; here courses arrive via the optional
 *  "courses" input — one course per line as "grade,credits,type"
 *  (type: regular|honors|ap_ib; e.g. "A,4,regular\nB+,3,honors"). */
fun calc_gpa_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()
    val isWeighted = inp.str("scaleSelect") == "weighted"

    var totalPts = 0.0
    var totalCredits = 0.0
    inp.str("courses").lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .forEach { line ->
            val p = line.split(",")
            val grade = p.getOrNull(0)?.trim()?.uppercase() ?: ""
            val cr = p.getOrNull(1)?.trim()?.toDoubleOrNull() ?: 0.0
            val type = p.getOrNull(2)?.trim()?.lowercase() ?: "regular"
            var pts = GPA_POINTS[grade] ?: 0.0
            if (isWeighted) {
                if (type == "ap_ib" || type == "ap" || type == "ib") pts += 1.0
                else if (type == "honors") pts += 0.5
            }
            val credits = if (cr > 0) cr else 0.0
            totalPts += pts * credits
            totalCredits += credits
        }

    val semGpa = if (totalCredits > 0) totalPts / totalCredits else 0.0
    out["resGpaValue"] = J.toFixed(semGpa, 2)
    out["resTotalCredits"] = J.s(totalCredits)
    out["resQualityPoints"] = J.toFixed(totalPts, 1)

    val priorGpa = inp.num("priorGpa")
    val priorCr = inp.num("priorCredits")
    val cumGpa = if (priorCr > 0) {
        val cumPts = priorGpa * priorCr + totalPts
        val cumCr = priorCr + totalCredits
        if (cumCr > 0) cumPts / cumCr else 0.0
    } else semGpa
    out["resCumGpa"] = J.toFixed(cumGpa, 2)

    out["resGpaStanding"] = when {
        semGpa >= 3.9 -> "Summa Cum Laude / Top Honors"
        semGpa >= 3.7 -> "Magna Cum Laude / Dean's List"
        semGpa >= 3.5 -> "Cum Laude / Dean's List"
        semGpa < 2.0 -> "Academic Warning / Review Needed"
        else -> "Good Academic Standing"
    }
    return out
}

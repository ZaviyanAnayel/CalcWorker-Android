package com.zaviyanllc.calcworker.data

/** Input widget kinds supported by the generic calculator screen. */
enum class InputKind { NUMBER, SLIDER, SELECT, SWITCH, DATE, TEXT, TEXTAREA }

data class CalcOption(val value: String, val label: String)

data class CalcInput(
    val id: String,
    val label: String,
    val kind: InputKind,
    val min: Double? = null,
    val max: Double? = null,
    val step: Double? = null,
    val def: String = "",
    val options: List<CalcOption> = emptyList(),
    /** For sliders that mirror a number field (e.g. tipSlider -> tipPct). */
    val bindTo: String? = null,
    val prefix: String? = null,
    val suffix: String? = null
)

data class CalcOutput(val id: String, val label: String)

enum class CalcKind { FORMULA, CURRENCY, OMNICALC }

data class CalcSpec(
    val slug: String,
    val title: String,
    val category: String,
    val keywords: List<String>,
    val blurb: String,
    val formula: String,
    val proTip: String,
    val inputs: List<CalcInput>,
    val outputs: List<CalcOutput>,
    val kind: CalcKind,
    val popular: Boolean = false,
    val currencyFrom: String? = null,
    val currencyTo: String? = null
)

/** Compact catalog entry used by the on-device AI helper and search. */
data class AiEntry(
    val title: String,
    val slug: String,
    val keywords: List<String>,
    val formula: String
)

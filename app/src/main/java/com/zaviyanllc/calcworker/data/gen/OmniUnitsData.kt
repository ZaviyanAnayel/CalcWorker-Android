package com.zaviyanllc.calcworker.data.gen

/** Unit conversion tables ported from OmniCalc Ultra. Auto-generated. */
data class OmniUnit(val code: String, val name: String, val factor: Double)
data class OmniCategory(val key: String, val name: String, val specialTemp: Boolean, val units: List<OmniUnit>)
object OmniUnitsData {
  val categories: List<OmniCategory> = listOf(
    OmniCategory("currency", "Currency", false, listOf(
      OmniUnit("USD", "US Dollar ($)", 1.0),
      OmniUnit("EUR", "Euro (€)", 0.92),
      OmniUnit("GBP", "British Pound (£)", 0.79),
      OmniUnit("JPY", "Japanese Yen (¥)", 154.5),
      OmniUnit("CAD", "Canadian Dollar (C$)", 1.37),
      OmniUnit("AUD", "Australian Dollar (A$)", 1.52),
      OmniUnit("CHF", "Swiss Franc (CHF)", 0.9),
      OmniUnit("INR", "Indian Rupee (₹)", 83.5),
      OmniUnit("CNY", "Chinese Yuan (¥)", 7.23),
      OmniUnit("AED", "UAE Dirham (AED)", 3.67),
    )),
    OmniCategory("length", "Length", false, listOf(
      OmniUnit("m", "Meters (m)", 1.0),
      OmniUnit("km", "Kilometers (km)", 1000.0),
      OmniUnit("cm", "Centimeters (cm)", 0.01),
      OmniUnit("mm", "Millimeters (mm)", 0.001),
      OmniUnit("mi", "Miles (mi)", 1609.344),
      OmniUnit("yd", "Yards (yd)", 0.9144),
      OmniUnit("ft", "Feet (ft)", 0.3048),
      OmniUnit("in", "Inches (in)", 0.0254),
      OmniUnit("nmi", "Nautical Miles", 1852.0),
    )),
    OmniCategory("mass", "Mass & Weight", false, listOf(
      OmniUnit("kg", "Kilograms (kg)", 1.0),
      OmniUnit("g", "Grams (g)", 0.001),
      OmniUnit("mg", "Milligrams (mg)", 1e-06),
      OmniUnit("t", "Metric Tons (t)", 1000.0),
      OmniUnit("lb", "Pounds (lb)", 0.45359237),
      OmniUnit("oz", "Ounces (oz)", 0.028349523),
      OmniUnit("st", "Stone (st)", 6.35029),
    )),
    OmniCategory("temperature", "Temperature", true, listOf(
      OmniUnit("C", "Celsius (°C)", 1.0),
      OmniUnit("F", "Fahrenheit (°F)", 1.0),
      OmniUnit("K", "Kelvin (K)", 1.0),
    )),
    OmniCategory("speed", "Speed", false, listOf(
      OmniUnit("m_s", "Meters / second (m/s)", 1.0),
      OmniUnit("km_h", "Kilometers / hour (km/h)", 0.277777778),
      OmniUnit("mph", "Miles / hour (mph)", 0.44704),
      OmniUnit("knot", "Knots (kn)", 0.514444),
      OmniUnit("mach", "Mach (std)", 340.29),
    )),
    OmniCategory("data", "Digital Data", false, listOf(
      OmniUnit("b", "Bits (b)", 1.0),
      OmniUnit("B", "Bytes (B)", 8.0),
      OmniUnit("KB", "Kilobytes (KB)", 8192.0),
      OmniUnit("MB", "Megabytes (MB)", 8388608.0),
      OmniUnit("GB", "Gigabytes (GB)", 8589934592.0),
      OmniUnit("TB", "Terabytes (TB)", 8796093022208.0),
    )),
    OmniCategory("time", "Time", false, listOf(
      OmniUnit("ms", "Milliseconds (ms)", 0.001),
      OmniUnit("s", "Seconds (s)", 1.0),
      OmniUnit("min", "Minutes (min)", 60.0),
      OmniUnit("hr", "Hours (hr)", 3600.0),
      OmniUnit("day", "Days (d)", 86400.0),
      OmniUnit("wk", "Weeks (wk)", 604800.0),
      OmniUnit("yr", "Years (yr - 365d)", 31536000.0),
    )),
    OmniCategory("area", "Area", false, listOf(
      OmniUnit("sq_m", "Square Meters (m²)", 1.0),
      OmniUnit("sq_km", "Square Kilometers (km²)", 1000000.0),
      OmniUnit("sq_ft", "Square Feet (ft²)", 0.092903),
      OmniUnit("sq_yd", "Square Yards (yd²)", 0.836127),
      OmniUnit("acre", "Acres (ac)", 4046.86),
      OmniUnit("ha", "Hectares (ha)", 10000.0),
    )),
    OmniCategory("volume", "Volume", false, listOf(
      OmniUnit("l", "Liters (L)", 1.0),
      OmniUnit("ml", "Milliliters (mL)", 0.001),
      OmniUnit("cu_m", "Cubic Meters (m³)", 1000.0),
      OmniUnit("gal", "Gallons (US)", 3.78541),
      OmniUnit("qt", "Quarts (US)", 0.946353),
      OmniUnit("pt", "Pints (US)", 0.473176),
      OmniUnit("cup", "Cups (US)", 0.236588),
      OmniUnit("fl_oz", "Fluid Ounces (fl oz)", 0.0295735),
    )),
  )
}
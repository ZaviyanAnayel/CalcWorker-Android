# CalcWorker — Native Android App

**CalcWorker** by Zaviyan. 100+ calculators, offline-first, built with Kotlin + Jetpack Compose + Material 3.

- Package: `com.zaviyanllc.calcworker`
- Min SDK 24 · Target/Compile SDK 34
- Themes: Deep Dark + Dim
- No WebView. All formulas run natively on-device.

## Build

```bash
./gradlew assembleRelease   # APK -> app/build/outputs/apk/release/
./gradlew bundleRelease     # AAB -> app/build/outputs/bundle/release/
```

CI (`.github/workflows/`) builds a signed APK and AAB on every push to `main`.

## Structure

- `app/src/main/java/com/zaviyanllc/calcworker/calc/` — 93 native formula ports (generated from the website + hand-written)
- `app/src/main/java/com/zaviyanllc/calcworker/data/` — specs registry, FX repo (live + cached), OmniCalc engine, AI client
- `app/src/main/java/com/zaviyanllc/calcworker/ui/` — Compose screens (Home, Calculator, Currency, OmniCalc, AI, About)

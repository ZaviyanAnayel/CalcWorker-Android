# CalcWorker — Android App

Production Android app for [CalcWorker](https://calcworker.com) — 102 free calculators
as a native-feeling app, built with [Capacitor](https://capacitorjs.com).

- **App ID:** `com.zaviyanllc.calcworker`
- **App name:** CalcWorker
- **Version:** 1.0.0 (versionCode 1)

## How it works

The app loads the **live site** `https://calcworker.com` inside a native WebView
(Capacitor `server.url`), so the app is always current — no need to ship an update
when the website adds a calculator.

It is deliberately **not** a thin website wrapper, so it passes Google Play's
"Spam and Minimum Functionality" review:

| Native behavior | Implementation |
|---|---|
| Branded splash screen | `@capacitor/splash-screen` + generated `drawable/splash` |
| Themed status bar (dark, light icons) | Injected bridge (`www/cw-bridge.js`) via `MainActivity` |
| Offline page with Retry — never a blank white screen | `server.errorPath: offline.html` (native) + `Network` plugin mid-session detection |
| Back button walks WebView history; double-tap to exit on home | Injected bridge via `@capacitor/app` `backButton` event |
| External links open in the system browser / Custom Tabs | `allowNavigation` limited to `calcworker.com`; injected bridge routes the rest to `@capacitor/browser` |
| Light haptic tick on calculator button taps | Injected bridge via `@capacitor/haptics` |

`www/` contains only the bundled support files (`offline.html`, `cw-bridge.js`,
icon) — the 102 calculators are **not** bundled; they load from the live site.

## Project layout

```
calcworker-app/
├── capacitor.config.json      # server.url, errorPath, splash config
├── www/                       # offline.html, cw-bridge.js, icon (synced into the APK/AAB)
├── android/                   # Capacitor Android project (committed)
│   └── app/src/main/
│       ├── java/.../MainActivity.java   # injects cw-bridge.js on every page load
│       └── res/               # launcher icons (mdpi–xxxhdpi), adaptive icons, splash
├── .github/workflows/build-aab.yml      # manual signed-AAB build (CI)
├── store-listing/             # Play Console listing pack
└── release/                   # local signed AAB output (git-ignored)
```

## Local development

```bash
npm install
npx cap sync android     # copies www/ into the Android project
# open android/ in Android Studio to run on a device/emulator
```

## Release signing

The upload keystore is **never** committed. It lives only:

1. With the developer (in a password manager), and
2. As the `KEYSTORE_BASE64` GitHub Actions secret (for CI builds).

### Creating the secrets (one-time)

1. Base64-encode the keystore (no newlines):
   `base64 -w0 release.keystore` → copy the output.
2. In GitHub: repo **Settings → Secrets and variables → Actions → New repository secret**:
   - `KEYSTORE_BASE64` → the base64 output from step 1
   - `KEYSTORE_PASSWORD` → keystore password
   - `KEY_ALIAS` → key alias (e.g. `calcworker-upload`)
   - `KEY_PASSWORD` → key password
3. Run the **Build signed release AAB** workflow (Actions → manual dispatch) and
   download the `.aab` artifact — this is what gets uploaded to Play Console.

### Local signed build

```bash
export KEYSTORE_FILE=/path/to/release.keystore   # never commit this file
# then provide KEYSTORE_PASSWORD / KEY_ALIAS / KEY_PASSWORD as env vars
cd android
./gradlew bundleRelease \
  -Pandroid.injected.signing.store.file="$KEYSTORE_FILE" \
  -Pandroid.injected.signing.store.password="$KEYSTORE_PASSWORD" \
  -Pandroid.injected.signing.key.alias="$KEY_ALIAS" \
  -Pandroid.injected.signing.key.password="$KEY_PASSWORD"
# output: android/app/build/outputs/bundle/release/app-release.aab
```

> Passwords are passed via environment only — never written to files or committed.

## Google Play submission checklist

- [ ] Finish the $25 Google Play developer registration
- [ ] Create the app in Play Console: title/description from `store-listing/listing.md`
- [ ] Upload the signed `.aab` (first upload registers the upload key)
- [ ] Privacy policy URL: `https://www.calcworker.com/privacy`
- [ ] Data Safety form: answers drafted in `store-listing/data-safety.md`
- [ ] Content rating questionnaire: notes in `store-listing/content-rating-notes.md`
- [ ] Screenshots: 2+ phone screenshots (1080×1920 min) + 1024×500 feature graphic
      (compose from `store-listing/feature-graphic.html`)
- [ ] Review testing notes: `store-listing/testing-notes.md` (no login required)

## Roadmap (v2)

- **Push notifications** (new calculator alerts): needs a Firebase project and
  `google-services.json` — intentionally not half-wired in v1. Add
  `@capacitor/push-notifications`, wire FCM, then resubmit.
- **AdMob**: the website's AdSense does not work inside apps. If in-app ads are
  wanted later, integrate the AdMob SDK as a separate update.
- **Home-screen widget** (quick access to favorite calculators).

## Constraints respected

- The `calcworker.com` website source and its ad code were not modified.
- No secrets are stored in this repo (keystore/passwords excluded via `.gitignore`).

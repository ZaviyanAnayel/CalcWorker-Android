# Data Safety form — draft answers (CalcWorker v1.0.0)

> DRAFT — review against the live Play Console questionnaire before submitting.
> Facts below were derived from the app source and the live site's privacy
> policy (https://www.calcworker.com/privacy), which explicitly discloses
> Google Analytics (GA4, tag G-1QCQNSCVQM) and Google AdSense
> (publisher ca-pub-3405098265613384).

## App-level facts
- The app's **native code collects nothing**: no accounts, no login, no forms
  submitted to the developer, no analytics SDK, no crash SDK, no FCM/AdMob SDK
  in v1.0.0.
- The app loads `https://calcworker.com` in a WebView. That website uses:
  - **Google Analytics 4** → device identifiers, approximate location, usage /
    interaction data, for analytics.
  - **Google AdSense** → advertising ID / cookies, approximate location,
    for personalized advertising.
- All network traffic is HTTPS (cleartext disabled; `allowMixedContent: false`).

## Suggested questionnaire answers

**Does your app collect or share any of the required user data types?**
→ **Yes** — via the website's third-party services (Google Analytics, Google
AdSense). The app code itself collects nothing.

**Data types collected** (by Google's SDKs/scripts on the loaded site):

| Data type | Collected | Shared | Purpose |
|---|---|---|---|
| Device or other IDs (advertising ID) | Yes | Yes (Google) | Advertising / marketing, Analytics |
| Approximate location | Yes | Yes (Google) | Advertising / marketing, Analytics |
| App activity → App interactions | Yes | Yes (Google) | Analytics |
| Photos and videos / Files and docs / Contacts / etc. | No | No | — |
| Personal info (name, email, phone) | No | No | — |

**Follow-up questions:**
- *Is this data collected, shared, or both?* — Collected by the app (WebView)
  and shared with Google (service providers for analytics/ads).
- *Is collection required, or can users opt out?* — Not required for core
  functionality (calculators work regardless). Users can opt out of ad
  personalization via Android's "Delete advertising ID" / "Opt out of Ads
  Personalization" device setting.
- *Is data encrypted in transit?* — **Yes** (HTTPS only).
- *Can users request data deletion?* — Analytics/ad data is held by Google;
  users can manage it via Google's ad settings and GA opt-out controls.
  The developer holds no user data to delete (no accounts).
- *Does the app follow the Families Policy?* — Not a children's app; target
  audience is general (13+).

## Privacy policy URL (for the store listing)
`https://www.calcworker.com/privacy`

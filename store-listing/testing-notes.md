# Review testing notes — for the Play Console "App access" / review team

## Credentials
**No login required.** Every feature in the app is available without an
account. Leave any username/password fields in Play Console empty.

## How to exercise the app (what reviewers should check)
1. Launch → branded splash → homepage of CalcWorker loads with the tool grid.
2. Tap any calculator card → calculator page opens and is fully interactive.
3. Use the search bar → typeahead suggestions filter the 102 tools.
4. Open the AI "Need help?" assistant → answers questions about the tools.
5. Press the system Back button → walks back through page history; on the
   home page, a "Press back again to exit" toast appears; second press exits.
6. Tap any link that leaves calcworker.com → opens in the system browser
   (Custom Tabs), not trapped in the app.
7. Turn on airplane mode and relaunch (or pull connectivity mid-session) →
   a branded **offline page** appears with a working **Retry** button. The app
   never shows a blank white screen. Turn connectivity back on → Retry (or
   automatic reconnect) reloads the site.

## Special notes
- The app loads the live site `https://calcworker.com`; content updates on the
  website appear in the app without an app update.
- No demo account, no VPN, no region lock, no special setup needed.
- Target audience: general (13+). No children's content.
- Contact for review issues: via the support URL on the store listing.

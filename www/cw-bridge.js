/* CalcWorker native bridge — injected by MainActivity on every main-frame page load.
 * Idempotent: safe to evaluate multiple times.
 * Responsibilities:
 *  - Theme the status bar to the CalcWorker dark header (#06080d, light icons).
 *  - Open off-site links (anything outside calcworker.com) in the system
 *    browser / Custom Tabs instead of trapping them in the WebView.
 *  - Light haptic tick on calculator button taps.
 *  - Navigate to the bundled offline page when connectivity drops mid-session.
 *  - Back button: walk WebView history; double-tap to exit when history is empty.
 */
(function () {
  if (window.__cwBridgeInstalled) return;
  window.__cwBridgeInstalled = true;

  function plugins() {
    return (window.Capacitor && window.Capacitor.Plugins) || {};
  }

  /* ---------- Status bar theming (dark header, light icons) ---------- */
  try {
    var SB = plugins().StatusBar;
    if (SB) {
      if (SB.setOverlaysWebView) SB.setOverlaysWebView({ overlay: false }).catch(function () {});
      if (SB.setBackgroundColor) SB.setBackgroundColor({ color: '#06080d' }).catch(function () {});
      // Style.Light = light icons/text, for dark backgrounds.
      if (SB.setStyle) SB.setStyle({ style: 'LIGHT' }).catch(function () {});
    }
  } catch (e) { /* plugin unavailable — ignore */ }

  /* ---------- External links -> system browser ---------- */
  document.addEventListener('click', function (ev) {
    var a = ev.target && ev.target.closest ? ev.target.closest('a[href]') : null;
    if (!a) return;
    var raw = a.getAttribute('href');
    if (!raw || raw.charAt(0) === '#') return;
    var url;
    try { url = new URL(raw, window.location.href); } catch (e) { return; }
    if (url.protocol !== 'http:' && url.protocol !== 'https:') return; // mailto:/tel: -> OS handlers
    var host = url.hostname.toLowerCase().replace(/^www\./, '');
    if (host === 'calcworker.com') return; // internal navigation stays in the WebView
    ev.preventDefault();
    ev.stopPropagation();
    var B = plugins().Browser;
    if (B && B.open) { B.open({ url: url.href }).catch(function () {}); }
    else { window.open(url.href, '_blank'); }
  }, true);

  /* ---------- Light haptic on calculator button taps ---------- */
  document.addEventListener('click', function (ev) {
    var t = ev.target && ev.target.closest
      ? ev.target.closest('button, input[type="button"], input[type="submit"], [role="button"], .btn')
      : null;
    if (!t) return;
    var H = plugins().Haptics;
    if (H && H.impact) {
      try { H.impact({ style: 'LIGHT' }).catch(function () {}); } catch (e) {}
    }
  }, true);

  /* ---------- Offline detection mid-session ---------- */
  function goOffline() {
    if (window.location.href.indexOf('offline.html') !== -1) return;
    window.location.href = 'file:///android_asset/public/offline.html';
  }
  try {
    var N = plugins().Network;
    if (N && N.getStatus) {
      N.getStatus().then(function (s) { if (s && s.connected === false) goOffline(); }).catch(function () {});
      if (N.addListener) {
        N.addListener('networkStatusChange', function (s) { if (s && s.connected === false) goOffline(); });
      }
    } else {
      window.addEventListener('offline', goOffline);
    }
  } catch (e) {
    window.addEventListener('offline', goOffline);
  }

  /* ---------- Floating Home button (one-tap, one-handed) ----------
   * Shows on every tool/guide page (not on the homepage or offline page).
   * 56dp touch target, bottom-left with safe-area margin, semi-transparent
   * until touched so it never shouts over the site's own UI.
   */
  try {
    var path = window.location.pathname.replace(/\/+$/, '') || '/';
    var isHome = path === '/' || path === '/index.html' || path === '/index';
    var isOffline = window.location.href.indexOf('offline.html') !== -1;
    if (!isHome && !isOffline) {
      var fab = document.createElement('button');
      fab.setAttribute('type', 'button');
      fab.setAttribute('aria-label', 'Back to CalcWorker home');
      fab.innerHTML =
        '<svg width="26" height="26" viewBox="0 0 24 24" fill="none" ' +
        'stroke="#ffffff" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">' +
        '<path d="M3 10.5 12 3l9 7.5"/><path d="M5 9.5V21h14V9.5"/>' +
        '<path d="M9.5 21v-6h5v6"/></svg>';
      fab.style.cssText =
        'position:fixed;left:16px;bottom:calc(24px + env(safe-area-inset-bottom, 0px));' +
        'width:56px;height:56px;border-radius:50%;border:0;cursor:pointer;z-index:2147483646;' +
        'display:flex;align-items:center;justify-content:center;' +
        'background:linear-gradient(135deg,#0284c7 0%,#2563eb 50%,#7c3aed 100%);' +
        'box-shadow:0 10px 28px rgba(37,99,235,.5),0 0 0 1px rgba(56,189,248,.35);' +
        'opacity:.72;transition:opacity .2s ease,transform .12s ease;' +
        '-webkit-tap-highlight-color:transparent;';
      var fabReset;
      fab.addEventListener('pointerdown', function () {
        fab.style.opacity = '1';
        fab.style.transform = 'scale(.93)';
        clearTimeout(fabReset);
      });
      fab.addEventListener('pointerup', function () {
        fab.style.transform = 'scale(1)';
        fabReset = setTimeout(function () { fab.style.opacity = '.72'; }, 1200);
      });
      fab.addEventListener('click', function () {
        window.location.href = 'https://calcworker.com/';
      });
      // Insert late so page styles settle first; never block page load.
      setTimeout(function () {
        if (document.body) document.body.appendChild(fab);
      }, 600);
    }
  } catch (e) { /* home button is a convenience — never break the page */ }

  /* ---------- Back button: history first, double-tap to exit ---------- */
  try {
    var App = plugins().App;
    if (App && App.addListener) {
      var lastBack = 0;
      var toastEl = null;
      var toastTimer = 0;
      function toast(msg) {
        if (!toastEl) {
          toastEl = document.createElement('div');
          toastEl.style.cssText =
            'position:fixed;left:50%;bottom:72px;transform:translateX(-50%);' +
            'background:rgba(17,23,38,.96);color:#f8fafc;font-size:13px;font-weight:600;' +
            'padding:10px 18px;border-radius:999px;z-index:2147483647;white-space:nowrap;' +
            'border:1px solid rgba(56,189,248,.35);box-shadow:0 8px 24px rgba(0,0,0,.5);' +
            'opacity:0;transition:opacity .18s ease;pointer-events:none;';
          document.body.appendChild(toastEl);
        }
        toastEl.textContent = msg;
        toastEl.style.opacity = '1';
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { toastEl.style.opacity = '0'; }, 1600);
      }
      App.addListener('backButton', function (data) {
        if (data && data.canGoBack) { window.history.back(); return; }
        var now = Date.now();
        if (now - lastBack < 2000) {
          try { App.exitApp(); } catch (e2) {}
        } else {
          lastBack = now;
          toast('Press back again to exit');
        }
      });
    }
  } catch (e) { /* plugin unavailable — OS default back behavior applies */ }
})();

package com.zaviyanllc.calcworker;

import android.os.Bundle;
import android.webkit.WebView;
import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.BridgeWebViewClient;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * CalcWorker main activity.
 *
 * The remote site (https://calcworker.com) is loaded via Capacitor's server.url.
 * A custom BridgeWebViewClient subclass injects www/cw-bridge.js into every
 * CalcWorker page after load. The bridge wires up native-feeling behavior:
 * status-bar theming, external links in the system browser, haptics on button
 * taps, mid-session offline detection, and history-aware back-button handling.
 *
 * Load failures are handled by Capacitor itself via server.errorPath
 * (capacitor.config.json) which shows the bundled offline.html — never a
 * blank white page.
 */
public class MainActivity extends BridgeActivity {

    private static final String BRIDGE_ASSET = "public/cw-bridge.js";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bridge bridge = getBridge();
        if (bridge == null) {
            return;
        }

        bridge.setWebViewClient(
            new BridgeWebViewClient(bridge) {
                private String bridgeJs;

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (url != null && isCalcWorkerUrl(url)) {
                        if (bridgeJs == null) {
                            bridgeJs = loadBridgeJs();
                        }
                        if (bridgeJs != null && !bridgeJs.isEmpty()) {
                            view.evaluateJavascript(bridgeJs, null);
                        }
                    }
                }

                private boolean isCalcWorkerUrl(String url) {
                    return url.startsWith("https://calcworker.com")
                        || url.startsWith("https://www.calcworker.com");
                }

                private String loadBridgeJs() {
                    try (
                        InputStream is = getAssets().open(BRIDGE_ASSET);
                        BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, StandardCharsets.UTF_8)
                        )
                    ) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append('\n');
                        }
                        return sb.toString();
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        );
    }
}
